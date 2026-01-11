package com.ndiii.alert.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndiii.alert.domain.AlertEvent;
import com.ndiii.alert.domain.RuleType;
import com.ndiii.alert.repo.AlertEventRepository;
import com.ndiii.alert.repo.AlertRuleRepository;
import com.ndiii.common.api.TelemetryNormalizedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AlertEvaluator {

  private final AlertRuleRepository rules;
  private final AlertEventRepository events;
  private final ObjectMapper om = new ObjectMapper();

  public AlertEvaluator(AlertRuleRepository rules, AlertEventRepository events) {
    this.rules = rules;
    this.events = events;
  }

  @KafkaListener(topics = "telemetry.normalized", groupId = "alert-service")
  public void onTelemetry(String json) throws Exception {
    TelemetryNormalizedEvent ev = om.readValue(json, TelemetryNormalizedEvent.class);

    for (var r : rules.findByEnabledTrue()) {
      if (r.getDeviceId() != null && !r.getDeviceId().equals(ev.deviceId())) continue;

      if (r.getType() == RuleType.THRESHOLD) {
        if (r.getMetric() == null || r.getThreshold() == null) continue;
        Object v = ev.metrics().get(r.getMetric());
        if (v instanceof Number n && n.doubleValue() > r.getThreshold()) {
          events.save(new AlertEvent(ev.deviceId(),
              "THRESHOLD breached: " + r.getMetric() + "=" + n + " > " + r.getThreshold()));
        }
      }

      if (r.getType() == RuleType.RATE_OF_CHANGE) {
        // Minimal placeholder: ROC requires state/history; implement with Influx query or in-memory window.
        // Keeping as a scaffold for portfolio.
      }
    }
  }
}
