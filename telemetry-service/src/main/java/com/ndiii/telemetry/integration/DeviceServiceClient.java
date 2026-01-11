package com.ndiii.telemetry.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class DeviceServiceClient {

  private final RestClient client;

  public DeviceServiceClient(@Value("${app.deviceServiceUrl}") String baseUrl) {
    this.client = RestClient.builder().baseUrl(baseUrl).build();
  }

  @CircuitBreaker(name = "deviceService")
  public boolean validateApiKey(String deviceId, String apiKey) {
    Map<?, ?> resp = client.get()
        .uri(uriBuilder -> uriBuilder
            .path("/devices/internal/validateApiKey")
            .queryParam("deviceId", deviceId)
            .queryParam("apiKey", apiKey)
            .build())
        .retrieve()
        .body(Map.class);
    Object valid = resp == null ? null : resp.get("valid");
    return Boolean.TRUE.equals(valid);
  }
}
