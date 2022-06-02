package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.data.*;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchIDResponse;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieRepo
{
    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate template;

    private static String searchByMovieID =
            "SELECT m.id, m.title, m.year, p.name, m.rating, " +
                    "       m.num_votes, m.budget, m.revenue, m.overview, " +
                    "       m.backdrop_path, m.poster_path, m.hidden, " +
                    "       (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', g.id, 'name', g.name)) " +
                    "    FROM (SELECT DISTINCT g.id, g.name " +
                    "    FROM movies.genre g " +
                    "        JOIN movies.movie_genre mg ON g.id = mg.genre_id " +
                    "    WHERE mg.movie_id = :movieId " +
                    "    ORDER BY g.name) AS g) AS genres, " +
                    "       (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', p.id, 'name', p.name)) " +
                    "        FROM (SELECT DISTINCT p.id, p.name, p.popularity " +
                    "            FROM movies.person p " +
                    "            JOIN movies.movie_person mp ON p.id = mp.person_id " +
                    "            WHERE mp.movie_id = :movieId " +
                    "            ORDER BY p.popularity DESC, p.id) AS p) AS persons " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p ON m.director_id = p.id " +
                    "WHERE m.id = :movieId ";

    private static String base =
            "SELECT JSON_ARRAYAGG(JSON_OBJECT(" +
                    "'id', m.id," +
                    "'title', m.title," +
                    "'year', m.year," +
                    "'director', m.name," +
                    "'rating', m.rating," +
                    "'backdropPath', m.backdrop_path," +
                    "'posterPath', m.poster_path," +
                    "'hidden', m.hidden)) AS moviesJSON" +
                    " FROM " +
                    "(SELECT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                    "FROM movies.movie m JOIN movies.person p ON m.director_id = p.id ";

    //constructor
    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.objectMapper = objectMapper;
        this.template = template;
    }


    public MovieSearchResponse getMovie(
            String title, String director, String genre, Integer year, Integer limit, Integer page,
            String orderBy, String direction, Integer isAdmin, Long personID) {

        StringBuilder         sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();

        //keep track if a param has already been added
        int flag = 0;

        //add base to the sql string
        sql = new StringBuilder(base);

        if (personID != null) {
            sql.append("JOIN movies.movie_person mp ON m.id = mp.movie_id ");
        }
        if (genre != null) {
            sql.append(
                    "JOIN movies.movie_genre mg ON m.id = mg.movie_id " +
                            "JOIN movies.genre g ON mg.genre_id = g.id " +
                            "WHERE g.name LIKE :genre ");
            flag += 1;
        }

        //search by title
        if (title != null) {

            if (flag == 0) { //flag = 0 --> WHERE, flag != 0 --> AND
                sql.append("WHERE ");

            }
            else {
                sql.append("AND ");

            }
            sql.append("m.title LIKE :title ");
            flag += 1;

        }
        if (year != null) {
            if (flag == 0) { //flag = 0 --> WHERE, flag != 0 --> AND
                sql.append("WHERE ");

            }
            else {
                sql.append("AND ");

            }
            sql.append("m.year = :year ");
            flag += 1;
        }

        if (director != null) {
            if (flag == 0) { //flag = 0 --> WHERE, flag != 0 --> AND
                sql.append("WHERE ");

            }
            else {
                sql.append("AND ");

            }
            sql.append("p.name LIKE :director ");
            flag += 1;
        }


        if (personID != null) {
            if (flag == 0) { //flag = 0 --> WHERE, flag != 0 --> AND
                sql.append("WHERE ");

            }
            else {
                sql.append("AND ");

            }
            sql.append("mp.person_id = :personID ");
            flag += 1;
        }

        //if user is admin
        if (isAdmin == 0) {
            sql.append("AND m.hidden != true ");
        }
        //add order by
        MovieOrderBy order = MovieOrderBy.fromString(orderBy);
        sql.append(order.toSql());
        //add direction
        if (direction != null) {
            sql.append("DESC ");
        }
        sql.append(", m.id ");

        //add limit
        MovieLimit lim = MovieLimit.fromInt(limit);
        sql.append(lim.toSql());
        if (limit == null) {
            limit = 10;
        }

        //add offset
        Integer offset;
        if (page != null && page > 1) {
            offset = (page - 1) * limit;
            String s = String.format("OFFSET %d ", offset);
            sql.append(s);
        }

        sql.append(") as m;");

        //add source value
        if (title != null) {
            String wildcardSearch = '%' + title + '%';
            source.addValue("title", wildcardSearch, Types.VARCHAR);
        }
        if (year != null) {
            source.addValue("year", year, Types.INTEGER);
        }
        if (director != null) {
            String wildcard = '%' + director + '%';
            source.addValue("director", wildcard, Types.VARCHAR);
        }
        if (genre != null) {
            source.addValue("genre", genre, Types.VARCHAR);
        }
        if (personID != null) {
            source.addValue("personID", personID, Types.BIGINT);
        }

        //FINAL QUERY
        try {
            MovieSearchResponse response = this.template.queryForObject(
                    sql.toString(),
                    source,
                    this::methodInsteadOfLambdaForMapping
            );
            if (personID != null) {
                response.setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND);
            }
            else {
                response.setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH);
            }
            return response;
        }
        catch (Exception e) {
            if (personID != null) {
                throw new ResultError(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND);
            }
            else {
                throw new ResultError(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH);
            }
        }
    }

    public MovieSearchIDResponse getMovieByMovieID(Long movieId, Integer integer, Integer integer1, String s, String s1, int isAdmin) {
        StringBuilder sql = new StringBuilder(searchByMovieID);
        if (isAdmin == 0) {
            sql.append("AND m.hidden != true; ");
        }
        else {
            sql.append("; ");
        }
        MapSqlParameterSource source  = new MapSqlParameterSource()
                .addValue("movieId", movieId.intValue(), Types.INTEGER);

        try {
            return this.template.queryForObject(
                    sql.toString(),
                    source,
                    this::mapMovieIdSearch
            );
        }
        catch (Exception e) {
            throw new ResultError(MoviesResults.NO_MOVIE_WITH_ID_FOUND);
        }

    }


private MovieSearchIDResponse mapMovieIdSearch(ResultSet rs, int rowNumber) throws SQLException {
        Movie2 movie = new Movie2()
                .setId(rs.getLong("id"))
                .setTitle(rs.getString("title"))
                .setYear(rs.getInt("year"))
                .setDirector(rs.getString("name"))
                .setRating(rs.getDouble("rating"))
                .setNumVotes(rs.getInt("num_votes"))
                .setBudget(rs.getLong("budget"))
                .setRevenue(rs.getBigDecimal("revenue"))
                .setOverview(rs.getString("overview"))
                .setBackdropPath(rs.getString("backdrop_path"))
                .setPosterPath(rs.getString("poster_path"))
                .setHidden(rs.getBoolean("hidden"));

        List<Genre> genres = null;
        List<Person> persons = null;


        try {
            //retrieves the json string from the query from result set
            String g = rs.getString("genres");
            String p = rs.getString("persons");

            Genre[] genreArray =
                    objectMapper.readValue(g, Genre[].class);
            Person[] personArray =
                    objectMapper.readValue(p, Person[].class);

            // This just helps convert from an Object Array to a List<>
            genres = Arrays.stream(genreArray).collect(Collectors.toList());
            persons = Arrays.stream(personArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error: Movie Repo line 160");
        }

        MovieSearchIDResponse response = new MovieSearchIDResponse()
                .setResult(MoviesResults.MOVIE_WITH_ID_FOUND)
                .setMovie(movie)
                .setGenres(genres)
                .setPersons(persons);
        return response;
}

    private MovieSearchResponse methodInsteadOfLambdaForMapping(ResultSet rs, int rowNumber)
            throws SQLException
    {
        List<Movie> movies = null;


        try {
            //retrieves the json string from the query from result set
            String jsonArrayString = rs.getString("moviesJSON");

            Movie[] movieArray =
                    objectMapper.readValue(jsonArrayString, Movie[].class);

            // This just helps convert from an Object Array to a List<>
            movies = Arrays.stream(movieArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error: Movie Repo line 160");
        }

        return new MovieSearchResponse()
                .setMovies(movies);
    }


}
