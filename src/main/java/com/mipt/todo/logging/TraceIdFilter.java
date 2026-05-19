package com.mipt.todo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);
  public static final String TRACE_ID_HEADER = "X-Trace-Id";
  public static final String TRACE_ID_MDC_KEY = "traceId";
  private static final int MAX_LENGTH = 64;
  private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_.]+$");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String traceId = request.getHeader(TRACE_ID_HEADER);

    if (isValidTraceId(traceId)) {
      MDC.put(TRACE_ID_MDC_KEY, traceId);
      response.setHeader(TRACE_ID_HEADER, traceId);
    } else {
      if (traceId != null && !traceId.isBlank()) {
        log.warn("Invalid X-Trace-Id received (length={}, contains forbidden chars) – generating new UUID",
                traceId.length());
      }
      String generated = UUID.randomUUID().toString();
      MDC.put(TRACE_ID_MDC_KEY, generated);
      response.setHeader(TRACE_ID_HEADER, generated);
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(TRACE_ID_MDC_KEY);
    }
  }

  private boolean isValidTraceId(String traceId) {
    if (traceId == null || traceId.isBlank()) {
      return false;
    }
    if (traceId.length() > MAX_LENGTH) {
      return false;
    }
    return ALLOWED_PATTERN.matcher(traceId).matches();
  }
}