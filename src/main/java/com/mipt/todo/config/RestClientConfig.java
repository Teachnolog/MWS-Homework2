package com.mipt.todo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  public RestClient externalRestClient(
      @Value("${external.api.base-url:http://localhost:8080/external/v1}") String baseUrl,
      @Value("${external.api.connect-timeout-ms:1000}") int connectTimeoutMs,
      @Value("${external.api.read-timeout-ms:1500}") int readTimeoutMs) {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeoutMs);
    requestFactory.setReadTimeout(readTimeoutMs);

    return RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("User-Agent", "MWS-Homework2-Gateway/1.0")
        .requestFactory(requestFactory)
        .build();
  }
}


