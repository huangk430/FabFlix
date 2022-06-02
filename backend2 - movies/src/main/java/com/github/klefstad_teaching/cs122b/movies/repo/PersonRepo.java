package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.data.*;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchIDResponse;
import com.github.klefstad_teaching.cs122b.movies.response.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.response.PersonSearchIDResponse;
import com.github.klefstad_teaching.cs122b.movies.response.PersonSearchResponse;
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
public class PersonRepo {
    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate template;

    private static String searchById =
//            "SELECT JSON_OBJECT('id', p.id, 'name', p.name, 'birthday', p.birthday, 'biography', p.biography, " +
//                    "                   'birthday', p.birthday, 'birthplace', p.birthplace, 'popularity', p.popularity, 'profile_path', p.profile_path) " +
//                    "AS personJSON " +
//                    "FROM (SELECT " +
//                    "p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path " +
//                    "FROM movies.person p " +
//                    "WHERE p.id = :personId) as p;";
    "SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path FROM movies.person p WHERE p.id = :personId;";

    private static String base =
            "SELECT JSON_ARRAYAGG(JSON_OBJECT('id', p.id, 'name', p.name, 'birthday', p.birthday, 'biography', p.biography, " +
                    "    'birthday', p.birthday, 'birthplace', p.birthplace, 'popularity', p.popularity, 'profilePath', p.profile_path)) " +
                    "AS personJSON " +
                    "FROM (SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path " +
                    "FROM movies.person p ";


    //constructor
    @Autowired
    public PersonRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template) {
        this.objectMapper = objectMapper;
        this.template = template;
    }


    public PersonSearchResponse getPersons(String name, String birthday, String movieTitle, Integer limit, Integer page, String orderBy, String direction) {
        StringBuilder sql;
        MapSqlParameterSource source = new MapSqlParameterSource();

        //add base to the sql string
        sql = new StringBuilder(base);

        if (name != null) {
            sql.append("WHERE p.name LIKE :name ");
        }

        if (birthday != null) {
            sql.append("WHERE p.birthday = :birthday ");
        }

        if (movieTitle != null) {
            sql.append(
                    "JOIN movies.movie_person mp ON mp.person_id = p.id " +
                            "JOIN movies.movie m ON m.id = mp.movie_id " +
                            "WHERE m.title LIKE :movieTitle ");
        }

        //add order by
        PersonOrderBy order = PersonOrderBy.fromString(orderBy);
        sql.append(order.toSql());
        //add direction
        if (direction != null) {
            sql.append("DESC ");
        }
        sql.append(", p.id ");

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

        sql.append(") as p;");

        //add source value
        if (name != null) {
            String wildcardName = '%' + name + '%';
            source.addValue("name", wildcardName, Types.VARCHAR);
        }
        if (birthday != null) {
            source.addValue("birthday", birthday, Types.VARCHAR);
        }
        if (movieTitle != null) {
            String wildcardMovie = '%' + movieTitle + '%';
            source.addValue("movieTitle", wildcardMovie, Types.VARCHAR);
        }


        //FINAL QUERY
        try {
            PersonSearchResponse response = this.template.queryForObject(
                    sql.toString(),
                    source,
                    this::mapPerson
            );

            response.setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH);
            return response;
        } catch (Exception e) {
            throw new ResultError(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH);

        }

    }

    private PersonSearchResponse mapPerson(ResultSet rs, int rowNumber)
            throws SQLException {
        List<Person2> persons = null;


        try {
            //retrieves the json string from the query from result set
            String jsonArrayString = rs.getString("personJSON");

            Person2[] personArray =
                    objectMapper.readValue(jsonArrayString, Person2[].class);

            // This just helps convert from an Object Array to a List<>
            persons = Arrays.stream(personArray).collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error: Person Repo");
        }

        return new PersonSearchResponse()
                .setPersons(persons);

    }

    public PersonSearchIDResponse getPersonById(Long personId) {
        String sql = searchById;
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("personId", personId.intValue(), Types.INTEGER);

        //FINAL QUERY
        try {
            PersonSearchIDResponse response = this.template.queryForObject(
                    sql,
                    source,
                    this::mapPersonById
            );
            return response;




        } catch (Exception e) {
            throw new ResultError(MoviesResults.NO_PERSON_WITH_ID_FOUND);
        }
    }

    private PersonSearchIDResponse mapPersonById(ResultSet rs, int rowNumber) throws SQLException {
        Person2 person = new Person2()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"))
                .setBirthday(rs.getString("birthday"))
                .setBiography(rs.getString("biography"))
                .setBirthplace(rs.getString("birthplace"))
                .setPopularity(rs.getFloat("popularity"))
                .setProfilePath(rs.getString("profile_path"));

        PersonSearchIDResponse response = new PersonSearchIDResponse()
                .setPerson(person)
                .setResult(MoviesResults.PERSON_WITH_ID_FOUND);
        return response;
    }
}


