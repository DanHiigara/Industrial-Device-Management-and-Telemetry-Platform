package com.ndiii.device.repo;

import com.ndiii.device.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {
  boolean existsByApiKey(String apiKey);
}
