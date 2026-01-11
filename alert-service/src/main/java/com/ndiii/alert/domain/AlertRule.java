package com.ndiii.alert.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "alert_rules")
public class AlertRule {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RuleType type;

  // applies to metric key for THRESHOLD/RATE
  private String metric;

  // for threshold: max allowed; for roc: max delta per minute
  private Double threshold;

  // applies to deviceId; null = all devices
  private String deviceId;

  @Column(nullable = false)
  private boolean enabled = true;

  public Long getId() { return id; }
  public RuleType getType() { return type; }
  public String getMetric() { return metric; }
  public Double getThreshold() { return threshold; }
  public String getDeviceId() { return deviceId; }
  public boolean isEnabled() { return enabled; }

  public void setType(RuleType type) { this.type = type; }
  public void setMetric(String metric) { this.metric = metric; }
  public void setThreshold(Double threshold) { this.threshold = threshold; }
  public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
