package saviing.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import saviing.common.context.TraceContext;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 헬스체크/정적 리소스는 스킵
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator") || uri.startsWith("/assets/");
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        // 비동기 재디스패치 시엔 다시 실행하지 않음(요청당 1회 보장)
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {

        String traceId = TraceContext.hasTraceId()
            ? TraceContext.getTraceId()
            : TraceContext.generateNewTraceId();

        try {
            MDC.put("traceId", traceId);
            response.setHeader("X-Trace-Id", traceId);

            log.info("HTTP 요청 시작 - {} {}", request.getMethod(), request.getRequestURI());
            chain.doFilter(request, response);
            log.info("HTTP 요청 완료 - Status: {}", response.getStatus());

        } finally {
            MDC.remove("traceId");
            TraceContext.clear(); // 꼭 정리 (스레드 재사용 누수 방지)
        }
    }
}