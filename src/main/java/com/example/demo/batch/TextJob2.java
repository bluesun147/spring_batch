package com.example.demo.batch;

import com.example.demo.custom.CustomPassThroughLineAggregator;
import com.example.demo.dto.OneDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TextJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final int chunkSize = 5;

    // 실행 위한 job
    @Bean
    public Job textJob2_batchBuild() {
        return jobBuilderFactory.get("textJob2")
                .start(textJob2_batchStep1())
                .build();
    }

    @Bean
    public Step textJob2_batchStep1() {
        return stepBuilderFactory.get("textJob2_batchStep1")
                // <I, O>
                .<OneDto, OneDto>chunk(chunkSize)
                .reader(textJob2_FileReader())
                // .processor()
                .writer(textJob2_FileWriter())
                .build();
    }

    // 리더 - resources에 있는 txt 파일 읽기
    @Bean
    public FlatFileItemReader<OneDto> textJob2_FileReader() {
        FlatFileItemReader<OneDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("sample/textJob2_input.txt"));
        flatFileItemReader.setLineMapper(((line, lineNumber) -> new OneDto(lineNumber + "_" + line)));
        return flatFileItemReader;
    }

    // 라이터
    @Bean
    public FlatFileItemWriter textJob2_FileWriter() {
        return new FlatFileItemWriterBuilder<OneDto>()
                .name("textJob2_FileWriter")
                .resource(new FileSystemResource("output/textJob2_output.txt"))
                // 커스텀 한 lineAggregator
                // but! 추천하는 커스텀 방법은 x
                // 가장 올바른 커스텀 방법은 reader와 writer 사이에 processor 이용해서 하는 것
                .lineAggregator(new CustomPassThroughLineAggregator<>())
                .build();
    }
}
