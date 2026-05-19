package com.mipt.todo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AccessLogFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    long start = System.nanoTime();
    try {
      filterChain.doFilter(request, response);
    } finally {
      long tookNanos = System.nanoTime() - start;
      long tookMillis = tookNanos / 1_000_000;
      String traceId = MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY);
      log.info("HTTP {} {} -> status={} timeMs={} trace={}",
              request.getMethod(), request.getRequestURI(), response.getStatus(), tookMillis, traceId);
    }
  }
}