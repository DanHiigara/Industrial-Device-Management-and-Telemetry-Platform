package com.ndiii.device.web;

import com.ndiii.common.util.Ids;
import com.ndiii.device.domain.Device;
import com.ndiii.device.repo.DeviceRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/devices")
public class DeviceController {

  private final DeviceRepository repo;

  public DeviceController(DeviceRepository repo) {
    this.repo = repo;
  }

  public record CreateDeviceReq(@NotBlank String deviceId, @NotBlank String deviceType, @NotBlank String firmwareVersion) {}

  @PostMapping
  @PreAuthorize("hasAnyAuthority('SCOPE_role') or hasAnyRole('ADMIN','ENGINEER')") // gateway passes role claim
  public ResponseEntity<?> create(@RequestBody CreateDeviceReq req) {
    if (repo.existsById(req.deviceId())) return ResponseEntity.badRequest().body(Map.of("error", "device_exists"));

    Device d = new Device();
    d.setDeviceId(req.deviceId());
    d.setDeviceType(req.deviceType());
    d.setFirmwareVersion(req.firmwareVersion());
    d.setApiKey(Ids.apiKey(32));
    d.setLastSeen(Instant.now());
    repo.save(d);

    return ResponseEntity.ok(Map.of(
        "deviceId", d.getDeviceId(),
        "apiKey", d.getApiKey(),
        "deviceType", d.getDeviceType(),
        "firmwareVersion", d.getFirmwareVersion()
    ));
  }

  @GetMapping("/{deviceId}")
  public ResponseEntity<?> get(@PathVariable String deviceId) {
    return repo.findById(deviceId)
        .map(d -> ResponseEntity.ok(Map.of(
            "deviceId", d.getDeviceId(),
            "deviceType", d.getDeviceType(),
            "firmwareVersion", d.getFirmwareVersion(),
            "health", d.getHealth().name(),
            "lastSeen", d.getLastSeen()
        )))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{deviceId}")
  @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
  public ResponseEntity<?> delete(@PathVariable String deviceId) {
    repo.deleteById(deviceId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{deviceId}/heartbeat")
  public ResponseEntity<?> heartbeat(@PathVariable String deviceId) {
    return repo.findById(deviceId)
        .map(d -> {
          d.setLastSeen(Instant.now());
          repo.save(d);
          return ResponseEntity.ok(Map.of("status", "OK"));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  // Internal endpoint for telemetry-service API key validation
  @GetMapping("/internal/validateApiKey")
  public ResponseEntity<?> validateApiKey(@RequestParam String deviceId, @RequestParam String apiKey) {
    return repo.findById(deviceId)
        .map(d -> {
          boolean ok = d.getApiKey().equals(apiKey);
          return ResponseEntity.ok(Map.of("valid", ok));
        })
        .orElse(ResponseEntity.ok(Map.of("valid", false)));
  }
}
