package com.example.demo.csvSchedule;

import com.example.demo.csvSchedule.dto.ScheduleDto;
import com.example.demo.csvSchedule.entity.Schedule;
import com.example.demo.csvSchedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CsvScheduleWriter implements ItemWriter<ScheduleDto> {
    private final ScheduleRepository scheduleRepository;

    @Override
    public void write(List<? extends ScheduleDto> items) throws Exception {
        List<Schedule> scheduleList = new ArrayList<>();

        items.forEach(getScheduleDto -> {
            Schedule schedule = getScheduleDto.toEntity();
            scheduleList.add(schedule);
        });
        scheduleRepository.saveAll(scheduleList);
    }
}
