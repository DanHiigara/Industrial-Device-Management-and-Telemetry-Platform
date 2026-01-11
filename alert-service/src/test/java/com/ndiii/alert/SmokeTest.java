package com.ndiii.alert;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Smoke tests that start full Spring contexts are better suited as integration tests
 * executed in an environment where required dependencies (PostgreSQL/Influx/Kafka)
 * are available (e.g., via docker-compose).
 *
 * In CI/unit-test phase we keep tests fast and deterministic, so this class is disabled.
 */
@Disabled("Run as integration test with docker-compose dependencies available")
class SmokeTest {
  @Test void contextLoads() {}
}
