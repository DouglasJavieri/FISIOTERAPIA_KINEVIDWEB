package com.fisioterapiakinevid.kinevid.config.security;



import com.fisioterapiakinevid.kinevid.config.security.exception.JwtTokenExpiredException;
import com.fisioterapiakinevid.kinevid.config.security.exception.JwtTokenInvalidException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/02/2026
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${kinevid.app.jwtSecret}")
    private String jwtSecret;

    @Value("${kinevid.app.jwtAccessTokenExpirationMs}")
    private long jwtAccessTokenExpirationMs;

    @Value("${kinevid.app.jwtRefreshTokenExpirationMs}")
    private long jwtRefreshTokenExpirationMs;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateAccessToken(userDetails.getUsername());
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshTokenExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromAccessToken(String token) {
        return getUsernameFromToken(token, "ACCESS");
    }

    public String getUsernameFromRefreshToken(String token) {
        return getUsernameFromToken(token, "REFRESH");
    }

    private String getUsernameFromToken(String token, String expectedType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenType = (String) claims.get("type");
            if (tokenType == null || !tokenType.equals(expectedType)) {
                throw new JwtTokenInvalidException(
                        "Tipo de token invÃ¡lido. Esperado: " + expectedType +
                                ", Recibido: " + tokenType);
            }

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException("Token expirado", e);
        } catch (JwtException e) {
            throw new JwtTokenInvalidException("Token invÃ¡lido", e);
        }
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, "ACCESS");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "REFRESH");
    }

    private boolean validateToken(String authToken, String expectedType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            String tokenType = (String) claims.get("type");
            if (tokenType == null || !tokenType.equals(expectedType)) {
                log.warn("Tipo de token invÃ¡lido. Esperado: {}, Recibido: {}", expectedType, tokenType);
                return false;
            }

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT no soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT con formato invÃ¡lido: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Firma JWT invÃ¡lida: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Argumentos JWT invÃ¡lidos: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("Error JWT: {}", e.getMessage());
        }
        return false;
    }

    public long getExpirationTimeMs(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (JwtException e) {
            log.error("Error al obtener tiempo de expiraciÃ³n: {}", e.getMessage());
            return 0;
        }
    }


    /**
     * Obtiene el tiempo de expiraciÃ³n en milisegundos del Access Token
     * @return Tiempo de expiraciÃ³n en ms
     */
    public long getAccessTokenExpirationTime() {
        return jwtAccessTokenExpirationMs;
    }

    /**
     * Obtiene el tiempo de expiraciÃ³n en milisegundos del Refresh Token
     * @return Tiempo de expiraciÃ³n en ms
     */
    public long getRefreshTokenExpirationTime() {
        return jwtRefreshTokenExpirationMs;
    }

}

