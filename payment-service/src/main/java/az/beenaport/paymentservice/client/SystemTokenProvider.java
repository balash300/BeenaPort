package az.beenaport.paymentservice.client;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class SystemTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.system-token-expiry:3600}")
    private long systemTokenExpiry;

    public String generateSystemToken() {
        return Jwts.builder()
                .setSubject("billing-service")
                .claim("roles", List.of("SYSTEM"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + systemTokenExpiry * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}