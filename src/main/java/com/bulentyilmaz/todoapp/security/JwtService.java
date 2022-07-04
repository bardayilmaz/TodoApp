package com.bulentyilmaz.todoapp.security;

import com.bulentyilmaz.todoapp.config.SecurityConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtService { // token işlemlerinin yapıldığı class.

    private final SecurityConfig securityConfig;

    public JwtService(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    public Authentication verifyToken(String token) {
        if(StringUtils.isNoneEmpty(token) && token.startsWith("Bearer ")) {
            try {
                byte[] signingKey  = securityConfig.getJwtSecret().getBytes();
                Jws<Claims> parsedToken = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token.replace("Bearer ", ""));

                String subject = parsedToken
                        .getBody()
                        .getSubject();

                if(StringUtils.isNotEmpty(subject)) {
                    return new UsernamePasswordAuthenticationToken(subject, null, null);
                }
            } catch(ExpiredJwtException e) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, e.getMessage());
            } catch(UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch(MalformedJwtException exception) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch(SignatureException exception) {
                log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch(IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }
        return null;
    }

    public String createToken(String subject) {
        byte[] signingKey = securityConfig.getJwtSecret().getBytes();
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .setHeaderParam("typ", "JWT")
                .setIssuer("todo-app")
                .setAudience("todo-app")
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 10*24*60*60*1000))
                .compact();
    }
}
