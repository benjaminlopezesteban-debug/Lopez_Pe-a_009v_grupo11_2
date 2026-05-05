package com.proyecto.gestionArchivo.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    private final String issuer;
    private final long expirationMillis;
    private final Algorithm algorithm;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer:gestionArchivo}") String issuer,
            @Value("${security.jwt.expiration-ms:3600000}") long expirationMillis
    ) {
        this.issuer = issuer;
        this.expirationMillis = expirationMillis;
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMillis);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && isTokenValid(token);
    }

    public boolean isTokenValid(String token) {
        try {
            verifier().verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return decode(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        return decode(token).getExpiresAt().toInstant();
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }

    private DecodedJWT decode(String token) {
        return verifier().verify(token);
    }

    private JWTVerifier verifier() {
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }
}
