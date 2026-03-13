package com.king.lms.e_learning_hub.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.stereotype.Component;

import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class JwtUtils {

    @Value("${jwt.signerKey}")
    private String SIGN_KEY;

    public String generateToken(User user, long expiredMinutes, TokenType tokenType) {
        // Lấy chuỗi role ngăn cách bởi space
        String role = String.join(" ",user.getRoles().stream().map(r->r.getName()).toList());

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        
        JWTClaimsSet claimsBuilder = new JWTClaimsSet.Builder()
                                                            .subject(user.getUsername())
                                                            .issueTime(new Date())
                                                            .expirationTime(Date.from(Instant.now().plus(expiredMinutes,ChronoUnit.MINUTES)))
                                                            .jwtID(UUID.randomUUID().toString())
                                                            .claim("scope", role)
                                                            .claim("type", tokenType)
                                                            .build();

        try {
            SignedJWT signedJWT = new SignedJWT(header, claimsBuilder);

            JWSSigner jwsSigner = new MACSigner(SIGN_KEY.getBytes());

            signedJWT.sign(jwsSigner);

            return signedJWT.serialize();
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo token: "+e);
        }
    }

    public JWTClaimsSet validateToken(String token) throws ParseException, JOSEException{
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

        if(!signedJWT.verify(verifier))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        JWTClaimsSet claimsSet =  signedJWT.getJWTClaimsSet();

        if(claimsSet.getExpirationTime().before(new Date()))
            throw new AppException(ErrorCode.EXPIRED_TOKEN);

        return claimsSet;
    }


    public String getUserName(String token) throws ParseException, JOSEException{

        JWTClaimsSet claimsSet =  validateToken(token);

        return claimsSet.getSubject();

    }

}
