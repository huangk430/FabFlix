package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;

//for inserting into database
@Component
public class IDMRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template) {this.template = template;}

    public void updateRefreshTokenStatus(RefreshToken rt, int status) {
        this.template.update(
                "UPDATE idm.refresh_token" +
                        "   SET token_status_id = :status " +
                        "WHERE id = :id;",
                new MapSqlParameterSource()
                        .addValue("id", rt.getId(), Types.INTEGER)
                        .addValue("status", status, Types.INTEGER)
        );
    }

    public void updateRefreshTokenExpireTime(RefreshToken rt, Instant expireTime) {
        this.template.update(
                "UPDATE idm.refresh_token" +
                        "   SET expire_time = :expireTime " +
                        "WHERE id = :id;",
                new MapSqlParameterSource()
                        .addValue("id", rt.getId(), Types.INTEGER)
                        .addValue("expireTime", Timestamp.from(expireTime), Types.TIMESTAMP)
        );
    }

    public void insertRefreshToken(RefreshToken rt) {
        this.template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time)" +
                        "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time);",
                new MapSqlParameterSource()
                        .addValue("token", rt.getToken(), Types.VARCHAR)
                        .addValue("user_id", rt.getUserId(), Types.INTEGER)
                        .addValue("token_status_id", rt.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(rt.getExpireTime()), Types.TIMESTAMP)
                        .addValue("max_life_time", Timestamp.from(rt.getMaxLifeTime()), Types.TIMESTAMP)
        );

    }

    public RefreshToken findRefreshToken(String refreshToken) {
        try {
            return this.template.queryForObject(
                    "SELECT id, token, user_id, token_status_id, expire_time, max_life_time " +
                            "FROM idm.refresh_token " +
                            "WHERE token = :refreshToken;",

                    new MapSqlParameterSource()
                            .addValue("refreshToken", refreshToken, Types.VARCHAR),

                    (rs, rowNum) ->
                            new RefreshToken()
                                    .setId(rs.getInt("id"))
                                    .setToken(rs.getString("token"))
                                    .setUserId((rs.getInt("user_id")))
                                    .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                    .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                    .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
            );
        }
        //Refresh token not found
        catch (Exception exception) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public boolean userInsert(User user) {

        try {
            this.template.update(
                    "INSERT INTO idm.user (email, user_status_id, salt, hashed_password)" +
                            "VALUES (:email, :user_status_id, :salt, :hashed_password);",
                    new MapSqlParameterSource()
                            .addValue("email", user.getEmail(), Types.VARCHAR)
                            .addValue("user_status_id", user.getUserStatus().id(), Types.INTEGER)
                            .addValue("salt", user.getSalt(), Types.CHAR)
                            .addValue("hashed_password", user.getHashedPassword(), Types.CHAR)
            );
        }
        //if update did not update any rows, then user already exists
        catch (Exception exception) {
            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }
        //user successfully registered
        return true;
    }

    public User findUserFromEmail(String email) {

        //queryForObject will return one row, throws an error if not exactly 1 object is returned
        //uses email to extract user
        try {
            return this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE email = :email;",

                    new MapSqlParameterSource()
                            .addValue("email", email, Types.VARCHAR),

                    (rs, rowNum) ->
                            new User()
                                    .setId(Integer.valueOf(rs.getString("id")))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );
        }
        //user not found
        catch (Exception exception) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public User findUserFromID(Integer id) {

        //queryForObject will return one row, throws an error if not exactly 1 object is returned
        //uses id to extract user
        try {
            return this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE id = :id;",

                    new MapSqlParameterSource()
                            .addValue("id", id, Types.INTEGER),

                    (rs, rowNum) ->
                            new User()
                                    .setId(Integer.valueOf(rs.getString("id")))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );
        }
        //user not found
        catch (Exception exception) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }
}
