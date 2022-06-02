package com.github.klefstad_teaching.cs122b.idm.rest;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.model.AuthenticateRequest;
import com.github.klefstad_teaching.cs122b.idm.model.LoginResponse;
import com.github.klefstad_teaching.cs122b.idm.model.RefreshRequest;
import com.github.klefstad_teaching.cs122b.idm.model.RegisterRequest;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    public void errorCheck(@RequestBody RegisterRequest request)
    {
        //email length error
        if (request.getEmail().toString().length() < 6 || request.getEmail().toString().length() > 32) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }
        //email format error
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }

        //password length requirement
        if (request.getPassword().length < 10 || request.getPassword().length > 20) {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }

        //password char requirements
        String r = String.valueOf(request.getPassword());
        if (r.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$") == false)
        {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Validate> register(@RequestBody RegisterRequest request)
    {
        errorCheck(request);
        //if no errors, create and insert user into db
        authManager.createAndInsertUser(request.getEmail(), request.getPassword());

        //create body for response object
        Validate body = new Validate()
                .setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody RegisterRequest request) throws ParseException, JOSEException, BadJOSEException {
        //check for errors
        errorCheck(request);

        User user = authManager.selectAndAuthenticateUser(request.getEmail(), request.getPassword());

        //create access token & refresh token
        String accessToken = jwtManager.buildAccessToken(user);
        RefreshToken rt = jwtManager.buildRefreshToken(user);

        //insert refresh token into database
        authManager.insertRefreshToken(rt);


        LoginResponse body = new LoginResponse()
                .setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY)
                .setAccessToken(accessToken)
                .setRefreshToken(rt.getToken());

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request) throws ParseException, JOSEException, BadJOSEException {
        String accessToken = null;
        RefreshToken newRT = null;

        //verify refresh token
        RefreshToken refreshToken = authManager.verifyRefreshToken(request.getRefreshToken());

        //obtain user from user id
        User user = authManager.getUserFromRefreshToken(refreshToken);

        //update refresh token expire time
        this.jwtManager.updateRefreshTokenExpireTime(refreshToken);

        //check if expire time exceeds max expire time
        if (refreshToken.getExpireTime().compareTo(refreshToken.getMaxLifeTime()) > 0) {
            //update old refresh token status to revoked in db
            authManager.revokeRefreshToken(refreshToken);

            //create access token & refresh token
            accessToken = jwtManager.buildAccessToken(user);
            newRT = jwtManager.buildRefreshToken(user);

            //insert refresh token into database
            authManager.insertRefreshToken(newRT);

        }
        else {
            //update same refresh token expire time in db
            authManager.updateRefreshTokenExpireTime(refreshToken);

            //return same refresh token and new access token
            accessToken = jwtManager.buildAccessToken(user);
            newRT = refreshToken;

            //update current refresh token in db
            authManager.updateRefreshTokenExpireTime(newRT);
        }

        //return response
        LoginResponse body = new LoginResponse()
                .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN)
                .setAccessToken(accessToken)
                .setRefreshToken(newRT.getToken());

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Validate> authenticate(@RequestBody AuthenticateRequest request) throws ParseException, BadJOSEException, JOSEException {
        //check invalid format
        jwtManager.verifyAccessToken(request.getAccessToken());

        //check for expired
        SignedJWT signedJWT = SignedJWT.parse(request.getAccessToken());
        if (signedJWT.getJWTClaimsSet().getExpirationTime().compareTo(Date.from(Instant.now())) < 0) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
        }

        //return response
        Validate body = new Validate()
                .setResult(IDMResults.ACCESS_TOKEN_IS_VALID);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }
}
