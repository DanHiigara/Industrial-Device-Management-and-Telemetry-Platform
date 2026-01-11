package com.ndiii.sim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndiii.common.api.TelemetryPayload;
import picocli.CommandLine;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "device-sim",
    mixinStandardHelpOptions = true,
    description = "Java-based device simulator that posts telemetry payloads to the platform"
)
public class SimulatorMain implements Callable<Integer> {

  @CommandLine.Option(names = {"-u","--url"}, description = "Telemetry URL (gateway).", defaultValue = "http://localhost:8080/api/telemetry")
  String url;

  @CommandLine.Option(names = {"-d","--deviceId"}, description = "Device ID.", defaultValue = "WT-ESP32-001")
  String deviceId;

  @CommandLine.Option(names = {"-k","--apiKey"}, description = "Device API Key header.", required = true)
  String apiKey;

  @CommandLine.Option(names = {"-n","--count"}, description = "Number of messages.", defaultValue = "20")
  int count;

  @CommandLine.Option(names = {"-p","--periodMs"}, description = "Period between messages.", defaultValue = "500")
  int periodMs;

  private final ObjectMapper om = new ObjectMapper();
  private final HttpClient http = HttpClient.newHttpClient();
  private final Random rnd = new Random();

  @Override
  public Integer call() throws Exception {
    for (int i = 0; i < count; i++) {
      TelemetryPayload payload = new TelemetryPayload(
          deviceId,
          Instant.now(),
          20 + rnd.nextDouble() * 30,
          rnd.nextDouble() * 0.05,
          "OK"
      );

      String body = om.writeValueAsString(payload);

      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/json")
          .header("X-API-KEY", apiKey)
          .POST(HttpRequest.BodyPublishers.ofString(body))
          .build();

      HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
      System.out.println("[" + i + "] " + resp.statusCode() + " " + resp.body());

      Thread.sleep(periodMs);
    }
    return 0;
  }

  public static void main(String[] args) {
    int exit = new CommandLine(new SimulatorMain()).execute(args);
    System.exit(exit);
  }
}
