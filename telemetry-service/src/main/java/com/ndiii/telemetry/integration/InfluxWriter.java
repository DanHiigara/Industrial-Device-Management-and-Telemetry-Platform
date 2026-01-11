package com.ndiii.telemetry.integration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.ndiii.common.api.TelemetryPayload;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InfluxWriter {
  private final InfluxDBClient client;
  private final String bucket;
  private final String org;

  public InfluxWriter(
      @Value("${app.influx.url}") String url,
      @Value("${app.influx.token}") String token,
      @Value("${app.influx.org}") String org,
      @Value("${app.influx.bucket}") String bucket
  ) {
    this.client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    this.bucket = bucket;
    this.org = org;
  }

  public void writeTelemetry(TelemetryPayload p) {
    try (WriteApi api = client.makeWriteApi()) {
      Point point = Point.measurement("telemetry")
          .addTag("deviceId", p.deviceId())
          .time(p.timestamp() != null ? p.timestamp() : Instant.now(), WritePrecision.NS);
      if (p.temperature() != null) point.addField("temperature", p.temperature());
      if (p.vibration() != null) point.addField("vibration", p.vibration());
      if (p.status() != null) point.addField("status", p.status());
      api.writePoint(bucket, org, point);
    }
  }

  @PreDestroy
  public void shutdown() {
    client.close();
  }
}
