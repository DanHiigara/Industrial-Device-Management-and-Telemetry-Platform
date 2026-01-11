package com.ndiii.device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ndiii.device","com.ndiii.common"})
public class DeviceServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(DeviceServiceApplication.class, args);
  }
}
