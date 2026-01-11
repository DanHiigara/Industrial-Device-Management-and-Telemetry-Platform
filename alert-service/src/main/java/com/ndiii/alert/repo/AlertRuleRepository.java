package com.ndiii.alert.repo;

import com.ndiii.alert.domain.AlertRule;
import com.ndiii.alert.domain.RuleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
  List<AlertRule> findByEnabledTrue();
  List<AlertRule> findByEnabledTrueAndType(RuleType type);
}
