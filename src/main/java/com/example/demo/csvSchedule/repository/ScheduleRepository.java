package com.example.demo.csvSchedule.repository;

import com.example.demo.csvSchedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
