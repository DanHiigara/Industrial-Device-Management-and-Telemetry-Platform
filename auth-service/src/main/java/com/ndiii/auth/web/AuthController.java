package com.ndiii.auth.web;

import com.ndiii.auth.domain.UserAccount;
import com.ndiii.auth.domain.UserRole;
import com.ndiii.auth.repo.UserAccountRepository;
import com.ndiii.auth.security.JwtService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserAccountRepository repo;
  private final JwtService jwtService;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(UserAccountRepository repo, JwtService jwtService) {
    this.repo = repo;
    this.jwtService = jwtService;
  }

  public record RegisterReq(@Email String email, @NotBlank String password, UserRole role) {}
  public record LoginReq(@Email String email, @NotBlank String password) {}

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterReq req) {
    if (repo.existsByEmail(req.email())) return ResponseEntity.badRequest().body(Map.of("error", "email_exists"));

    UserAccount u = new UserAccount();
    u.setEmail(req.email());
    u.setPasswordHash(encoder.encode(req.password()));
    u.setRole(req.role() == null ? UserRole.ENGINEER : req.role());
    repo.save(u);

    String token = jwtService.issueAccessToken(u);
    return ResponseEntity.ok(Map.of("accessToken", token, "role", u.getRole().name()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req) {
    var userOpt = repo.findByEmail(req.email());
    if (userOpt.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
    var u = userOpt.get();
    if (!encoder.matches(req.password(), u.getPasswordHash())) return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));

    String token = jwtService.issueAccessToken(u);
    return ResponseEntity.ok(Map.of("accessToken", token, "role", u.getRole().name()));
  }
}
