package com.ndiii.auth.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class UserAccount {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() { return id; }
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public UserRole getRole() { return role; }

  public void setEmail(String email) { this.email = email; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public void setRole(UserRole role) { this.role = role; }
}
