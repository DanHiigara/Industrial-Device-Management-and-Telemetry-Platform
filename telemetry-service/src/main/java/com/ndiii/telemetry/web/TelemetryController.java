package com.ndiii.telemetry.web;

import com.ndiii.common.api.TelemetryNormalizedEvent;
import com.ndiii.common.api.TelemetryPayload;
import com.ndiii.telemetry.integration.DeviceServiceClient;
import com.ndiii.telemetry.integration.InfluxWriter;
import com.ndiii.telemetry.kafka.TelemetryProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

  private final DeviceServiceClient deviceClient;
  private final InfluxWriter influx;
  private final TelemetryProducer producer;
  private final ExecutorService ingestionExecutor;

  public TelemetryController(DeviceServiceClient deviceClient, InfluxWriter influx, TelemetryProducer producer, ExecutorService ingestionExecutor) {
    this.deviceClient = deviceClient;
    this.influx = influx;
    this.producer = producer;
    this.ingestionExecutor = ingestionExecutor;
  }

  @PostMapping
  public ResponseEntity<?> ingest(@RequestHeader(name = "X-API-KEY", required = false) String apiKey,
                                 @Valid @RequestBody TelemetryPayload payload) {
    if (apiKey == null || apiKey.isBlank()) return ResponseEntity.status(401).body(Map.of("error", "missing_api_key"));

    boolean valid = deviceClient.validateApiKey(payload.deviceId(), apiKey);
    if (!valid) return ResponseEntity.status(403).body(Map.of("error", "invalid_api_key"));

    try {
      ingestionExecutor.submit(() -> {
        influx.writeTelemetry(payload);

        Map<String, Object> metrics = new LinkedHashMap<>();
        if (payload.temperature() != null) metrics.put("temperature", payload.temperature());
        if (payload.vibration() != null) metrics.put("vibration", payload.vibration());

        producer.publish(new TelemetryNormalizedEvent(payload.deviceId(), payload.timestamp(), metrics, payload.status()));
      });
    } catch (RejectedExecutionException ex) {
      return ResponseEntity.status(429).body(Map.of("error", "backpressure", "message", "Ingestion queue full. Retry later."));
    }

    return ResponseEntity.accepted().body(Map.of("status", "ACCEPTED"));
  }
}
