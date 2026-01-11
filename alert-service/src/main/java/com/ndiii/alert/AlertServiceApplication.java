package com.ndiii.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.ndiii.alert","com.ndiii.common"})
@EnableScheduling
public class AlertServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AlertServiceApplication.class, args);
  }
}
