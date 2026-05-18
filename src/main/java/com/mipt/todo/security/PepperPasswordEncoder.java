package com.mipt.todo.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperPasswordEncoder implements PasswordEncoder {

  private final String pepper;
  private final BCryptPasswordEncoder delegate;

  public PepperPasswordEncoder(String pepper) {
    this.pepper = pepper;
    this.delegate = new BCryptPasswordEncoder();
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return delegate.encode(rawPassword.toString() + pepper);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return delegate.matches(rawPassword.toString() + pepper, encodedPassword);
  }
}

