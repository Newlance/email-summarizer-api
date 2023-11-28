package com.lmig.corporate.ignite2023submissions.soupemailapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class Config {

  @Bean
  public WebClient tokenClient() {
    return WebClient.builder().build();
  }
}
