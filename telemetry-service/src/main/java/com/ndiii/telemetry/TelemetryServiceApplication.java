package com.ndiii.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ndiii.telemetry","com.ndiii.common"})
public class TelemetryServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(TelemetryServiceApplication.class, args);
  }
}
