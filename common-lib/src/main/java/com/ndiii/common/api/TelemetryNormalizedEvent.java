package com.ndiii.common.api;

import java.time.Instant;
import java.util.Map;

public record TelemetryNormalizedEvent(
    String deviceId,
    Instant timestamp,
    Map<String, Object> metrics,
    String status
) {}
