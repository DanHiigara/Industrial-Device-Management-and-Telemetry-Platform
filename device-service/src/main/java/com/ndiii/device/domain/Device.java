package com.ndiii.device.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "devices")
public class Device {
  @Id
  private String deviceId;

  @Column(nullable = false)
  private String deviceType;

  @Column(nullable = false)
  private String firmwareVersion;

  @Column(nullable = false, unique = true)
  private String apiKey;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeviceStatus health = DeviceStatus.OK;

  @Column(nullable = false)
  private Instant lastSeen = Instant.EPOCH;

  public String getDeviceId() { return deviceId; }
  public String getDeviceType() { return deviceType; }
  public String getFirmwareVersion() { return firmwareVersion; }
  public String getApiKey() { return apiKey; }
  public DeviceStatus getHealth() { return health; }
  public Instant getLastSeen() { return lastSeen; }

  public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
  public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
  public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
  public void setApiKey(String apiKey) { this.apiKey = apiKey; }
  public void setHealth(DeviceStatus health) { this.health = health; }
  public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }
}
