package com.king.lms.e_learning_hub.util;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.google.common.hash.Hashing;
import com.king.lms.e_learning_hub.entity.Role;
import com.king.lms.e_learning_hub.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
    private final RedisUtils redisUtils;
    static String KEY = TokenType.refresh.toString()+":";
    static String BLACKLIST_REFRESH_TOKEN_KEY = "blacklist_refresh:";

    public String generateToken(User user, long expiredMinutes, TokenType tokenType) {
        // Lấy chuỗi role ngăn cách bởi space
        String role = String.join(" ",user.getRoles().stream().map(Role::getName).toList());

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        
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

    public JWTClaimsSet validateToken(String token, TokenType tokenType) throws ParseException, JOSEException{
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

        if(!signedJWT.verify(verifier))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        JWTClaimsSet claimsSet =  signedJWT.getJWTClaimsSet();

        if(claimsSet.getExpirationTime().before(new Date()))
            throw new AppException(ErrorCode.EXPIRED_TOKEN);

        String type = claimsSet.getStringClaim("type");
        if(!type.equals(tokenType.name()))
            throw new AppException((ErrorCode.INVALID_TOKEN));

        return claimsSet;
    }


    public String getUserName(String token,TokenType tokenType) throws ParseException, JOSEException{

        JWTClaimsSet claimsSet =  validateToken(token,tokenType);

        return claimsSet.getSubject();

    }

    public String getUserNameByAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return authentication.getName();
    }


    public void storeRefreshToken(String username,String refreshToken,int durationMinutes){

        String hashToken = hashToken(refreshToken);

        long  timeExpired = durationMinutes*60L;

        redisUtils.set(getRefreshTokenKey(username),hashToken,timeExpired);

    }

    public String hashToken(String token){
        return Hashing.sha256().hashString(token, StandardCharsets.UTF_8).toString();
    }

    public String getRefreshTokenKey(String username){
        return KEY+username;
    }

    public void deleteRefreshToken(String username){
        redisUtils.delete(getRefreshTokenKey(username));
    }

    public String getStringRole(Set<Role> roles){

        return String.join(" ",roles.stream().map(Role::getName).toList());

    }


}
