package com.example.demo.csvSchedule;

import com.example.demo.csvSchedule.dto.ScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://velog.io/@suk13574/spring-batch로-csv-파일-DB에-저장하기

@Configuration
@RequiredArgsConstructor
public class FileReaderJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CsvReader csvReader;
    private final CsvScheduleWriter csvScheduleWriter;

    // 데이터 처리할 row size
    private static final int chunkSize = 5;

    // 학사일정 저장 job
    // job은 여러 step 갖고있음
    @Bean
    public Job csvScheduleJob() {
        return jobBuilderFactory.get("csvScheduleJob")
                .start(csvScheduleReaderStep())
                .build();
    }

    // csv 파일 읽고 db에 쓰는 step
    @Bean
    public Step csvScheduleReaderStep() {
        return stepBuilderFactory.get("csvScheduleReaderStep")
                // reader에 넘겨줄 타입, writer에 넘겨줄 타입
                .<ScheduleDto, ScheduleDto> chunk(chunkSize)
                .reader(csvReader.csvScheduleReader())
                .writer(csvScheduleWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
