package com.ndiii.telemetry.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndiii.common.api.TelemetryNormalizedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TelemetryProducer {

  public static final String TOPIC = "telemetry.normalized";

  private final KafkaTemplate<String, String> kafka;
  private final ObjectMapper om = new ObjectMapper();

  public TelemetryProducer(KafkaTemplate<String, String> kafka) {
    this.kafka = kafka;
  }

  public void publish(TelemetryNormalizedEvent event) {
    try {
      String json = om.writeValueAsString(event);
      kafka.send(TOPIC, event.deviceId(), json);
    } catch (Exception e) {
      throw new RuntimeException("Failed to publish Kafka telemetry event", e);
    }
  }
}
