package com.github.klefstad_teaching.cs122b.billing.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.stripe.model.Order;
import com.stripe.model.terminal.Reader;
import data.Item;
import data.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import response.CartDetailsResponse;
import response.OrderDetailResponse;
import response.OrderListResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BillingRepo
{
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper objectMapper;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template, ObjectMapper objectMapper)
    {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public BigDecimal getDiscountedPrice(BigDecimal unit_price, Integer discount) {
        BigDecimal discountedUnitPrice = unit_price.multiply((BigDecimal.valueOf(1)
                .subtract(BigDecimal.valueOf(discount/100.0))));

        discountedUnitPrice = discountedUnitPrice.setScale(2, RoundingMode.DOWN);
        return discountedUnitPrice;
    }

    public void cartInsert(Integer userId, Integer movieId, Integer quantity) {


        try {
            this.template.update(
                    "INSERT INTO billing.cart (user_id, movie_id, quantity)" +
                            "VALUES (:userId, :movieId, :quantity);",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
                            .addValue("quantity", quantity, Types.INTEGER)

            );
        }
        //if update did not update any rows, then user already exists
        catch (Exception e) {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }

    }

    public void cartUpdate(Integer userId, Integer movieId, Integer quantity) {

         int rowsUpdated =  this.template.update(
                    "UPDATE billing.cart" +
                            "   SET quantity = :quantity " +
                            "WHERE user_id = :userId " +
                            "AND movie_id = :movieId ;",
                    new MapSqlParameterSource()
                            .addValue("quantity", quantity, Types.INTEGER)
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
            );
         if (rowsUpdated == 0) {
             throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void cartDelete(int userId, int movieId) {
        int rowsUpdated =  this.template.update(
                "DELETE FROM billing.cart " +
                        "WHERE user_id = :userId " +
                        "AND movie_id = :movieId;",
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER)
                        .addValue("movieId", movieId, Types.INTEGER)
        );
        if (rowsUpdated == 0) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void cartClear(int userId) {
        int rowsUpdated =  this.template.update(
                "DELETE FROM billing.cart " +
                        "WHERE user_id = :userId; ",
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER)
        );
        if (rowsUpdated == 0) {
            throw new ResultError(BillingResults.CART_EMPTY);
        }
    }

    public void getCartTotal(int userId) {

    }

    public CartDetailsResponse cartRetrieve(int userId, int isPremium) {
        String NOT_PREMIUM = "SELECT JSON_ARRAYAGG(JSON_OBJECT(\n" +
                "        'unitPrice', i.unit_price, 'quantity', i.quantity, 'movieId', i.movie_id, 'movieTitle', i.title,\n" +
                "        'backdropPath', i.backdrop_path, 'posterPath', i.poster_path))\n" +
                "AS items\n" +
                "FROM (SELECT mp.unit_price, c.quantity, c.movie_id, m.title, m.backdrop_path, m.poster_path\n" +
                "      FROM billing.cart c\n" +
                "               JOIN billing.movie_price mp ON mp.movie_id = c.movie_id\n" +
                "               JOIN movies.movie m ON mp.movie_id = m.id\n" +
                "      WHERE c.user_id = :userId) as i;";

        String PREMIUM = "SELECT JSON_ARRAYAGG(JSON_OBJECT(\n" +
                "        'unitPrice', i.unit_price, 'quantity', i.quantity, 'movieId', i.movie_id, 'movieTitle', i.title,\n" +
                "        'backdropPath', i.backdrop_path, 'posterPath', i.poster_path))\n" +
                "           AS items\n" +
                "FROM (SELECT TRUNCATE((mp.unit_price * (1 - (mp.premium_discount / 100.0))), 2) unit_price, c.quantity, c.movie_id, m.title, m.backdrop_path, m.poster_path\n" +
                "      FROM billing.cart c\n" +
                "               JOIN billing.movie_price mp ON mp.movie_id = c.movie_id\n" +
                "               JOIN movies.movie m ON mp.movie_id = m.id\n" +
                "      WHERE c.user_id = :userId) as i;";

        MapSqlParameterSource source     = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER);

        String sql = null;
        if (isPremium == 0) {
            sql = NOT_PREMIUM;
        }
        else {
            sql = PREMIUM;
        }
        try {
            CartDetailsResponse response = this.template.queryForObject(
                    sql,
                    source,
                    this::mapCartRetrieve
            );

            response.setResult(BillingResults.CART_RETRIEVED);

            return response;
        }
        catch (Exception e) {
            throw new ResultError(BillingResults.CART_EMPTY);
        }
    }

    private CartDetailsResponse mapCartRetrieve(ResultSet rs, int rowNumber)
            throws SQLException
    {
        List<Item> items = null;

        try {
            //retrieves the json string from the query from result set
            String i = rs.getString("items");

            Item[] genreArray =
                    objectMapper.readValue(i, Item[].class);

            // This just helps convert from an Object Array to a List<>
            items = Arrays.stream(genreArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error: Movie Repo line 160");
        }


        //calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            BigDecimal movie_total = (items.get(i).getUnitPrice()).multiply(BigDecimal.valueOf(items.get(i).getQuantity()));
            total = total.add(movie_total);
        }


        CartDetailsResponse response = new CartDetailsResponse()
                .setResult(BillingResults.CART_RETRIEVED)
                .setItems(items)
                .setTotal(total.setScale(2));

        return response;
    }


    public void createSaleRecord(Integer userId, BigDecimal total) {
        this.template.update(
                "INSERT INTO billing.sale (user_id, total, order_date)\n" +
                        "VALUES (:userId, :total, :orderDate);",
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER)
                        .addValue("total", total, Types.DECIMAL)
                        .addValue("orderDate", new Timestamp(System.currentTimeMillis()), Types.TIMESTAMP)

        );
    }

    public void createSaleItem(int userId, List<Item> items) {

        // get id of the last inputted order
        String sql = "SELECT id " +
                "FROM billing.sale " +
                "WHERE user_id = :user_id " +
                "ORDER BY ID DESC LIMIT 1 ";

        MapSqlParameterSource source =
                new MapSqlParameterSource()
                        .addValue("user_id", userId, Types.INTEGER);

        List<Item> billing_items =
                this.template.query(
                        sql,
                        source,
                        (rs, rowNum) ->
                                new Item()
                                        .setMovieId(rs.getLong("id"))
                );

        Integer sale_id = Math.toIntExact(billing_items.get(0).getMovieId());

        // create billing.sale_item entry
        for (Item item : items) {
            String saleItem = "INSERT INTO billing.sale_item (sale_id, movie_id, quantity) " +
                    "VALUES (:sale_id, :movie_id, :quantity) ";

            MapSqlParameterSource saleSource =
                    new MapSqlParameterSource()
                            .addValue("sale_id", sale_id, Types.INTEGER)
                            .addValue("movie_id", item.getMovieId(), Types.INTEGER)
                            .addValue("quantity", item.getQuantity(), Types.INTEGER);

            this.template.update(saleItem, saleSource);
        }
    }

    public OrderListResponse getSalesList(Integer userId) {
        String SALE_LIST =
                "SELECT s.id, s.total, s.order_date\n" +
                "FROM billing.sale s\n" +
                "WHERE user_id = :userId " +
                "ORDER BY s.id DESC LIMIT 5; ";

        MapSqlParameterSource source =
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER);

        List<Sale> sales =
                this.template.query(
                        SALE_LIST,
                        source,
                        (rs, rowNum) ->
                                new Sale()
                                        .setSaleId(rs.getLong("id"))
                                        .setTotal(rs.getBigDecimal("total"))
                                        .setOrderDate(rs.getTimestamp("order_date").toInstant())

                );

        //if sales is empty, no sales were found error
        if (sales.size() == 0) {
            throw new ResultError(BillingResults.ORDER_LIST_NO_SALES_FOUND);
        }

        OrderListResponse response = new OrderListResponse()
                .setResult(BillingResults.ORDER_LIST_FOUND_SALES)
                .setSales(sales);
        return response;

    }

    public OrderDetailResponse getSalesById(Integer userId, Integer saleId, int isPremium) {
        String GET_SALE =
                "SELECT m.id, quantity, unit_price, premium_discount, title, backdrop_path, poster_path " +
                        "FROM billing.sale s " +
                        "JOIN billing.sale_item si ON s.id = si.sale_id " +
                        "JOIN billing.movie_price mp ON mp.movie_id = si.movie_id " +
                        "JOIN movies.movie m ON m.id = mp.movie_id " +
                        " WHERE si.sale_id = :sale_id " +
                        " AND s.user_id = :user_id ";

        StringBuilder sql = new StringBuilder(GET_SALE);
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("sale_id", saleId, Types.INTEGER)
                .addValue("user_id", userId, Types.INTEGER);

        List<Item> items;
        if (isPremium == 1) {
            items =
                    this.template.query(
                            sql.toString(),
                            source,
                            (rs, rowNum) ->
                                    new Item()
                                            .setUnitPrice(this.getDiscountedPrice(rs.getBigDecimal("unit_price"),
                                                    rs.getInt("premium_discount")))
                                            .setQuantity(rs.getInt("quantity"))
                                            .setMovieId(rs.getLong("id"))
                                            .setMovieTitle(rs.getString("title"))
                                            .setBackdropPath(rs.getString("backdrop_path"))
                                            .setPosterPath(rs.getString("poster_path"))
                    );
        } else {
            items =
                    this.template.query(
                            sql.toString(),
                            source,
                            (rs, rowNum) ->
                                    new Item()
                                            .setUnitPrice(rs.getBigDecimal("unit_price"))
                                            .setQuantity(rs.getInt("quantity"))
                                            .setMovieId(rs.getLong("id"))
                                            .setMovieTitle(rs.getString("title"))
                                            .setBackdropPath(rs.getString("backdrop_path"))
                                            .setPosterPath(rs.getString("poster_path"))
                    );
        }

        if (items.size() == 0) {
            throw new ResultError(BillingResults.ORDER_DETAIL_NOT_FOUND);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            BigDecimal movie_total = (items.get(i).getUnitPrice()).multiply(BigDecimal.valueOf(items.get(i).getQuantity()));
            total = total.add(movie_total);
        }

        OrderDetailResponse response = new OrderDetailResponse()
                .setItems(items)
                .setTotal(total)
                .setResult(BillingResults.ORDER_DETAIL_FOUND);

        return response;
    }
}
