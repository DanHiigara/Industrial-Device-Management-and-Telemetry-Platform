package com.ndiii.common.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record TelemetryPayload(
    @NotBlank String deviceId,
    @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING) Instant timestamp,
    Double temperature,
    Double vibration,
    String status
) {}
