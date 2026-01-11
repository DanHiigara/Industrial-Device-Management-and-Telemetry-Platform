package com.ndiii.alert.repo;

import com.ndiii.alert.domain.AlertEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertEventRepository extends JpaRepository<AlertEvent, Long> {}
