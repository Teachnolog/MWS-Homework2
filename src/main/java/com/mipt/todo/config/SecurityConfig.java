package com.mipt.todo.config;

import com.mipt.todo.security.JwtAuthFilter;
import com.mipt.todo.security.PepperPasswordEncoder;
import com.mipt.todo.security.RestAccessDeniedHandler;
import com.mipt.todo.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ObjectProvider<JwtAuthFilter> jwtAuthFilterProvider,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler,
      @Value("${app.security.enabled:true}") boolean securityEnabled) throws Exception {

    if (!securityEnabled) {
      http.csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
      return http.build();
    }
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
              "/api/auth/login",
              "/api/v1/auth/login",
              "/external/**",
              "/actuator/health",
              "/error")
            .permitAll()
            .requestMatchers("/actuator/metrics/**").authenticated()
            .requestMatchers("/api/v1/profile").hasRole("USER")
            .requestMatchers("/api/v1/docs").hasAuthority("READ_PRIVILEGE")
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated())
        ;

    JwtAuthFilter jwtAuthFilter = jwtAuthFilterProvider.getIfAvailable();
    if (jwtAuthFilter != null) {
      http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder(@Value("${security.password.pepper}") String pepper) {
    return new PepperPasswordEncoder(pepper);
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.withUsername("user")
        .password(passwordEncoder.encode("password"))
        .authorities("ROLE_USER")
        .build();

    UserDetails reader = User.withUsername("reader")
        .password(passwordEncoder.encode("password"))
        .authorities("ROLE_USER", "READ_PRIVILEGE")
        .build();

    return new InMemoryUserDetailsManager(user, reader);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}