package com.mipt.todo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseDto {

  private final String accessToken;
  private final String tokenType;

  @JsonCreator
  public LoginResponseDto(@JsonProperty("accessToken") String accessToken) {
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