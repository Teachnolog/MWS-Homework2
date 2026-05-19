package com.mipt.todo.controller;

import com.mipt.todo.dto.LoginRequestDto;
import com.mipt.todo.dto.LoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.security.enabled=true",
        "TEST_USER_PASSWORD=password",
        "TEST_READER_PASSWORD=password"
})
class AuthControllerTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void login_withValidUser_returnsJwt() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/auth/login", loginRequest, LoginResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
    }

    @Test
    void login_withInvalidPassword_returnsUnauthorized() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("user");
        loginRequest.setPassword("wrong");

        ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/auth/login", loginRequest, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_withBlankUsername_returnsBadRequest() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("");
        loginRequest.setPassword("password");

        ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/auth/login", loginRequest, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void accessProtectedEndpoint_withoutToken_returnsUnauthorized() {
        ResponseEntity<Void> response = restTemplate.getForEntity(
                baseUrl() + "/api/tasks", Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void accessProtectedEndpoint_withValidToken_returnsOk() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        ResponseEntity<LoginResponseDto> loginResponse = restTemplate.postForEntity(
                baseUrl() + "/api/v1/auth/login", loginRequest, LoginResponseDto.class);
        String token = loginResponse.getBody().getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> tasksResponse = restTemplate.exchange(
                baseUrl() + "/api/tasks", HttpMethod.GET, entity, String.class);

        assertThat(tasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void accessProtectedEndpoint_withInvalidToken_returnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.token.value");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/api/tasks", HttpMethod.GET, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}