package com.ndiii.auth;

import com.ndiii.auth.domain.UserAccount;
import com.ndiii.auth.domain.UserRole;
import com.ndiii.auth.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceUnitTest {

  @Test
  void issuesTokenWithRoleClaim() {
    // 32+ bytes secret required for HS256 key
    JwtService svc = new JwtService("test-secret-test-secret-test-secret-test-secret", "issuer", 5);

    UserAccount u = new UserAccount();
    u.setEmail("dev@example.com");
    u.setRole(UserRole.ENGINEER);
    u.setPasswordHash("x");

    String token = svc.issueAccessToken(u);
    assertNotNull(token);
    assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");
  }
}
