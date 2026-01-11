package com.ndiii.alert.web;

import com.ndiii.alert.domain.AlertRule;
import com.ndiii.alert.domain.RuleType;
import com.ndiii.alert.repo.AlertEventRepository;
import com.ndiii.alert.repo.AlertRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/alerts")
public class RulesController {

  private final AlertRuleRepository rules;
  private final AlertEventRepository events;

  public RulesController(AlertRuleRepository rules, AlertEventRepository events) {
    this.rules = rules;
    this.events = events;
  }

  @PostMapping("/rules")
  public ResponseEntity<?> createRule(@RequestBody Map<String, Object> req) {
    AlertRule r = new AlertRule();
    r.setType(RuleType.valueOf(String.valueOf(req.get("type"))));
    r.setMetric((String) req.get("metric"));
    if (req.get("threshold") != null) r.setThreshold(Double.valueOf(String.valueOf(req.get("threshold"))));
    r.setDeviceId((String) req.get("deviceId"));
    r.setEnabled(req.get("enabled") == null || Boolean.parseBoolean(String.valueOf(req.get("enabled"))));
    rules.save(r);
    return ResponseEntity.ok(r);
  }

  @GetMapping("/rules")
  public ResponseEntity<?> listRules() {
    return ResponseEntity.ok(rules.findAll());
  }

  @GetMapping("/events")
  public ResponseEntity<?> listEvents() {
    return ResponseEntity.ok(events.findAll());
  }
}
