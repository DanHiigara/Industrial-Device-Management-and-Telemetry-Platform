package com.ndiii.alert.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "alert_events")
public class AlertEvent {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String deviceId;

  @Column(nullable = false)
  private String message;

  @Column(nullable = false)
  private Instant timestamp = Instant.now();

  public AlertEvent() {}

  public AlertEvent(String deviceId, String message) {
    this.deviceId = deviceId;
    this.message = message;
  }

  public Long getId() { return id; }
  public String getDeviceId() { return deviceId; }
  public String getMessage() { return message; }
  public Instant getTimestamp() { return timestamp; }
}
