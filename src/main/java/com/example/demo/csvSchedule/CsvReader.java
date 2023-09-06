package com.example.demo.csvSchedule;

import com.example.demo.csvSchedule.dto.ScheduleDto;
import com.example.demo.csvSchedule.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
public class CsvReader {
    // 파일 읽기
    @Bean
    public FlatFileItemReader<ScheduleDto> csvScheduleReader() {
        // 파일 읽기
        FlatFileItemReader<ScheduleDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csvSchedule/schedule.csv"));
        flatFileItemReader.setEncoding("UTF-8"); // 인코딩 설정

        // 읽으려는 데이터 LineMapper 통해 dto로 매핑
        DefaultLineMapper<ScheduleDto> defaultLineMapper = new DefaultLineMapper<>();

        // csv 파일 구분자
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(",");
        // 행으로 읽은 데이터 매칭할 각 이름
        delimitedLineTokenizer.setNames("month", "date", "event");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        // 매칭할 class 타입 지정
        BeanWrapperFieldSetMapper<ScheduleDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(ScheduleDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(defaultLineMapper);
        return flatFileItemReader;
    }
}