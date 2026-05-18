package com.mipt.todo.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.todo.dto.ExternalTaskCreateDto;
import com.mipt.todo.dto.ExternalTaskResponseDto;
import com.mipt.todo.exception.ExternalApiException;
import com.mipt.todo.exception.TaskNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

@Component
public class ExternalTasksClient {

  private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);

  private final RestClient externalRestClient;
  private final ObjectMapper objectMapper;

  public ExternalTasksClient(RestClient externalRestClient, ObjectMapper objectMapper) {
    this.externalRestClient = externalRestClient;
    this.objectMapper = objectMapper;
  }

  public URI createTask(ExternalTaskCreateDto request) {
    return externalRestClient.post()
        .uri("/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange((clientRequest, clientResponse) -> {
          HttpStatusCode status = clientResponse.getStatusCode();
          if (status.value() == 201) {
            URI location = clientResponse.getHeaders().getLocation();
            if (location == null) {
              throw new ExternalApiException("External API returned 201 without Location header");
            }
            return location;
          }
          String body = readBody(clientResponse);
          throw mapError(status, body, clientResponse.getHeaders(), null);
        });
  }

  public ExternalTaskResponseDto getTaskById(Long id) {
    return externalRestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/tasks/{id}").build(id))
        .accept(MediaType.APPLICATION_JSON)
        .exchange((clientRequest, clientResponse) -> {
          HttpStatusCode status = clientResponse.getStatusCode();
          String body = readBody(clientResponse);

          if (status.is2xxSuccessful()) {
            assertJsonContentType(clientResponse.getHeaders(), body, status);
            return deserialize(body, ExternalTaskResponseDto.class);
          }

          throw mapError(status, body, clientResponse.getHeaders(), id);
        });
  }

  public List<ExternalTaskResponseDto> getTasks(Boolean completed, Integer limit) {
    return externalRestClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/tasks")
            .queryParamIfPresent("completed", java.util.Optional.ofNullable(completed))
            .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
            .build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange((clientRequest, clientResponse) -> {
          HttpStatusCode status = clientResponse.getStatusCode();
          String body = readBody(clientResponse);

          if (status.is2xxSuccessful()) {
            assertJsonContentType(clientResponse.getHeaders(), body, status);
            return deserialize(body, new TypeReference<List<ExternalTaskResponseDto>>() {});
          }

          throw mapError(status, body, clientResponse.getHeaders(), null);
        });
  }

  public void deleteTask(Long id) {
    externalRestClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/tasks/{id}").build(id))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(status -> status.value() == 404, (request, response) -> {
          String body = readBody(response);
          throw mapError(response.getStatusCode(), body, response.getHeaders(), id);
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          String body = readBody(response);
          throw mapError(response.getStatusCode(), body, response.getHeaders(), id);
        })
        .toBodilessEntity();
  }

  private void assertJsonContentType(HttpHeaders headers, String body, HttpStatusCode status) {
    MediaType contentType = headers.getContentType();
    if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
      log.warn("Unexpected Content-Type from external API: status={} contentType={} body={}",
          status.value(), contentType, limitBody(body));
      throw new ExternalApiException("Unexpected content type from external API: " + contentType);
    }
  }

  private RuntimeException mapError(
      HttpStatusCode status,
      String body,
      HttpHeaders headers,
      Long id) {

    if (status.value() == 404) {
      String detail = extractProblemDetail(body);
      String message = id == null
          ? "Task not found in external API"
          : "Task " + id + " not found in external API";
      if (detail != null && !detail.isBlank()) {
        message = message + ": " + detail;
      }
      return new TaskNotFoundException(message);
    }

    if (status.value() == 429) {
      String retryAfter = headers.getFirst(HttpHeaders.RETRY_AFTER);
      return new ExternalApiException("External API rate limit reached. Retry-After=" + retryAfter);
    }

    if (status.is5xxServerError()) {
      if (headers.getContentType() != null
          && MediaType.TEXT_HTML.isCompatibleWith(headers.getContentType())) {
        log.warn("External API returned HTML body: {}", limitBody(body));
      }
      return new ExternalApiException("External API failure with status " + status.value());
    }

    return new ExternalApiException(
        "Unexpected status from external API: " + status.value() + ", body=" + limitBody(body));
  }

  private String extractProblemDetail(String body) {
    if (body == null || body.isBlank()) {
      return null;
    }
    try {
      JsonNode node = objectMapper.readTree(body);
      if (node.has("detail")) {
        return node.get("detail").asText();
      }
      return null;
    } catch (Exception ex) {
      return null;
    }
  }

  private <T> T deserialize(String body, Class<T> clazz) {
    try {
      return objectMapper.readValue(body, clazz);
    } catch (IOException ex) {
      throw new ExternalApiException("Failed to deserialize response from external API", ex);
    }
  }

  private <T> T deserialize(String body, TypeReference<T> typeReference) {
    try {
      return objectMapper.readValue(body, typeReference);
    } catch (IOException ex) {
      throw new ExternalApiException("Failed to deserialize response from external API", ex);
    }
  }

  private String readBody(org.springframework.http.client.ClientHttpResponse response) {
    try {
      if (response.getBody() == null) {
        return "";
      }
      return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
    } catch (IOException ex) {
      return "";
    }
  }

  private String limitBody(String body) {
    if (body == null) {
      return "";
    }
    int maxLen = 200;
    return body.length() <= maxLen ? body : body.substring(0, maxLen) + "...";
  }
}

