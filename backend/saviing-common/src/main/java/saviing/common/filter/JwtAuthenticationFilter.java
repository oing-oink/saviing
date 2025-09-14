package saviing.common.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import saviing.common.config.JwtConfig;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (!validateAuthHeader(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractTokenFromRequest(request);
            Claims tokenClaims = jwtConfig.parseToken(token);

            if (!validateAccessToken(tokenClaims)) {
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authentication = createAuthentication(tokenClaims);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
        } catch (SignatureException e) {
            log.debug("JWT signature validation failed: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("JWT token malformed: {}", e.getMessage());
        } catch (JwtException e) {
            log.debug("JWT token invalid: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateAuthHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authHeader)) {
            return false;
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            return false;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        return StringUtils.hasText(token);
    }

    private boolean validateAccessToken(Claims tokenClaims) {
        String tokenType = (String) tokenClaims.get("type");
        return "access".equals(tokenType);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER)
                .substring(BEARER_PREFIX.length());
    }

    private Authentication createAuthentication(Claims claims) {
        String customerId = claims.getSubject(); // customer_id

        // Role 없이 기본 권한만 부여
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("AUTHENTICATED"));

        return new UsernamePasswordAuthenticationToken(customerId, null, authorities);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 인증이 필요없는 경로들
        return path.startsWith("/v1/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }
}