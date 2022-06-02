package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Types;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

//authenticating user
@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    //searching for a user during login
    public User selectAndAuthenticateUser(String email, char[] password)
    {
        //extract user from database using IDMRepo::findUser() function
        User user = repo.findUserFromEmail(email);

        //user is banned or locked
        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new ResultError(IDMResults.USER_IS_BANNED);
        }
        if (user.getUserStatus() == UserStatus.LOCKED) {
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        }

        //generate hashed password from above user's salt and password given,
        //compare above user's hashed pass with the one calculated to authenticate

        String correctHashPass = user.getHashedPassword();
        String s = user.getSalt();
        byte[] HashPassBytes = hashPassword(password, s);
        String providedHashPass = Base64.getEncoder().encodeToString(HashPassBytes);
        //invalid credentials
        if (!correctHashPass.equals(providedHashPass)) {
            throw new ResultError(IDMResults.INVALID_CREDENTIALS);
        }
        return user;

    }

    public void createAndInsertUser(String email, char[] password)
    {
        byte[] salt = genSalt();
        byte[] hashedPass = hashPassword(password, salt);

        User user = new User()
                .setEmail(email)
                .setUserStatus(UserStatus.ACTIVE)
                .setSalt(Base64.getEncoder().encodeToString(salt))
                .setHashedPassword(Base64.getEncoder().encodeToString(hashedPass));

        //if inserting a user comes back here, then user was created succesfully
        repo.userInsert(user);

    }


    public void insertRefreshToken(RefreshToken refreshToken)
    {
        repo.insertRefreshToken(refreshToken);
    }

    //checks string that is retrieved from request, must be valid uuid format at 36 chars in length
    //retrieves refresh token from db if valid, and check if its expired or revoked
    //if valid, returns the RefreshToken object
    public RefreshToken verifyRefreshToken(String token)
    {
        //INVALID LENGTH
        if (token.length() != 36) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH);
        }

        //NOT A VALID UUID FORMAT
        try {
            UUID.fromString(token);

        } catch (Exception ex) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT);
        }

        //VALID: Retrieve from database
        RefreshToken refreshToken = repo.findRefreshToken(token);

        //check if expired
        if (refreshToken.getTokenStatus() == TokenStatus.EXPIRED) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        //check if revoked
        if (refreshToken.getTokenStatus() == TokenStatus.REVOKED) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }
        //check if current time is after expire time
        if (Instant.now().compareTo(refreshToken.getExpireTime()) > 0 || Instant.now().compareTo(refreshToken.getMaxLifeTime()) > 0) {
            //update refresh token to expired in db, 2 = expired
            repo.updateRefreshTokenStatus(refreshToken, 2);

            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }
        return refreshToken;
    }



    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        repo.updateRefreshTokenExpireTime(token, token.getExpireTime());
    }

    public void expireRefreshToken(RefreshToken token)
    {
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token, 3);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        return repo.findUserFromID(refreshToken.getUserId());
    }
}
