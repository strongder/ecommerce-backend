package com.example.shop.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtils {

    @NonFinal
    @Value("${jwt.secret}")
    private String SECRET;

    @NonFinal
    @Value("${jwt.refresh-secret}")
    private String REFRESH_SECRET;

    @NonFinal
    @Value("${jwt.expiration}")
    private int EXPIRATION;

    @NonFinal
    @Value("${jwt.refresh-expiration}")
    private int REFRESH_EXPIRATION;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName, SECRET, EXPIRATION);
    }


    private String createToken(Map<String, Object> claims, String userName, String secret, int expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(secret), SignatureAlgorithm.HS256).compact();
    }


    private Key getSignKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey(SECRET))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateRefreshToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName, REFRESH_SECRET, REFRESH_EXPIRATION);
    }



    public Claims extractAllClaimsRefresh(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey(REFRESH_SECRET))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isValidRefreshToken(String token) {
        try {
            Claims claims = extractAllClaimsRefresh(token);
            if (claims.getExpiration().after(new Date()) && claims.getSubject() != null) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
