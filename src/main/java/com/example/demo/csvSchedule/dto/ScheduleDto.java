package com.example.demo.csvSchedule.dto;

import com.example.demo.csvSchedule.entity.Schedule;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private String month;
    private String date;
    private String event;

    public Schedule toEntity() {
        return Schedule.builder()
            .month(this.month)
            .date(this.date)
            .event(this.event)
            .build();
    }
}
