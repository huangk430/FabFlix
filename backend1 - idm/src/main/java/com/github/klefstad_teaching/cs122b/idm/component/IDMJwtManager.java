package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
        this.jwtManager =
                new JWTManager.Builder()
                        .keyFileName(serviceConfig.keyFileName())
                        .accessTokenExpire(serviceConfig.accessTokenExpire())
                        .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                        .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                        .build();
    }

    //    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
//        throws JOSEException
//    {
//        return null;
//    }
//
    private void verifyJWT(String serialized)
            throws JOSEException, BadJOSEException
    {
        try {
            SignedJWT signedJWT = SignedJWT.parse(serialized);
            signedJWT.verify(this.jwtManager.getVerifier());
            this.jwtManager.getJwtProcessor().process(signedJWT, null);

            // Do logic to check if expired manually
            signedJWT.getJWTClaimsSet().getExpirationTime();


        } catch (ParseException |IllegalStateException | JOSEException | BadJOSEException e) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);

        }

    }

    public String buildAccessToken(User user)
            throws JOSEException, BadJOSEException {

        //payload
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getEmail()) //user email
                        //TODO not sure if this is right
                        .expirationTime(Date.from(Instant.now().plus(this.jwtManager.getAccessTokenExpire()))) //current time + accessTokenExpireTime
                        .issueTime(Date.from(Instant.now())) //current time
                        .claim(JWTManager.CLAIM_ID, user.getId())    // we set claims like values in a map
                        .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                        .build();

        JWSHeader header =
                new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                        .keyID(this.jwtManager.getEcKey().getKeyID())
                        .type(JWTManager.JWS_TYPE)
                        .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(this.jwtManager.getSigner());
        String serialized = signedJWT.serialize();

        this.verifyJWT(serialized);
        //serialized = string representation of accesstoken
        return serialized;
    }

    public void verifyAccessToken(String jws) throws BadJOSEException, JOSEException {
        this.verifyJWT(jws);

    }

    public RefreshToken buildRefreshToken(User user)
    {
        //build refresh token
        RefreshToken refreshToken = new RefreshToken()
                .setToken(UUID.randomUUID().toString())
                .setTokenStatus(TokenStatus.ACTIVE)
                .setUserId(user.getId())
                .setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()))
                .setMaxLifeTime(Instant.now().plus(this.jwtManager.getMaxRefreshTokenLifeTime()));


        return refreshToken;

    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        return false;
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        return false;
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        refreshToken.setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()));
    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
