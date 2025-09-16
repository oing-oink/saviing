package saviing.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtConfig {

    private final SecretKey secretKey;
    private final Duration tokenExpiry;
    private final JwtParser jwtParser;
    private final String refreshTokenCookieName;

    public JwtConfig(
            @Value("${jwt.secret:default-secret-key-for-development-only-please-change-in-production-minimum-32-characters}") String secret,
            @Value("${jwt.token-expiry:PT15M}") Duration tokenExpiry,
            @Value("${jwt.refresh-token-cookie-name:refresh_token}") String refreshTokenCookieName) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenExpiry = tokenExpiry;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    // Access Token - 헤더에서 사용
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, "access");
    }

    // Refresh Token - 쿠키에서 사용
    public String generateRefreshToken(String subject) {
        return generateToken(subject, Map.of(), "refresh");
    }

    private String generateToken(String subject, Map<String, Object> claims, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .claim("type", type)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenExpiry)))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    // Refresh Token을 HttpOnly + Secure 쿠키로 생성
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(tokenExpiry)
                .build();
    }

    // 로그아웃 시 쿠키 삭제용
    public ResponseCookie createExpiredRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }

    public long getTokenExpiryInSeconds() {
        return tokenExpiry.toSeconds();
    }

    public String getRefreshTokenCookieName() {
        return refreshTokenCookieName;
    }

    public String getSubjectFromToken(String token) {
        return parseToken(token).getSubject();
    }
}