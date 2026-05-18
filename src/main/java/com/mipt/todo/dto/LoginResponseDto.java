package com.mipt.todo.dto;

public class LoginResponseDto {

  private final String accessToken;
  private final String tokenType;

  public LoginResponseDto(String accessToken) {
    this.accessToken = accessToken;
    this.tokenType = "Bearer";
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }
}

