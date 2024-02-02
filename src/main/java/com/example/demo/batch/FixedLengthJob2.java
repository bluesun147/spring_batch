package com.example.demo.batch;

import com.example.demo.custom.CustomBeanWrapperFieldExtractor;
import com.example.demo.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

// csv 파일 쓰기

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FixedLengthJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final int chunkSize = 5;

    // job
    @Bean
    public Job fixedLengthJob2_batchBuild() throws Exception {
        return jobBuilderFactory.get("fixedLengthJob2")
                .start(fixedLengthJob2_batchStep1())
                .build();
    }

    // step
    @Bean
    public Step fixedLengthJob2_batchStep1() throws Exception {
        return stepBuilderFactory.get("fixedLengthJob2_batchStep1")
                .<TwoDto, TwoDto>chunk(chunkSize)
                .reader(fixedLengthJob2_FileReader())
                // .processor()
                .writer(fixedLengthJob2_FileWriter(new FileSystemResource("output/fixedLengthJob2_output.csv")))
                .build();
    }

    // 리더 - fixedLength 파일 읽기
    @Bean
    public FlatFileItemReader<TwoDto> fixedLengthJob2_FileReader() {
        FlatFileItemReader<TwoDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/sample/fixedLengthJob2_input.txt"));
        // csv 파일 맨 윗줄의 컬럼명 빼고 데이터만 읽기 위해
        flatFileItemReader.setLinesToSkip(1);

        DefaultLineMapper<TwoDto> dtoDefaultLineMapper = new DefaultLineMapper<>();
        FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();
        // 컬럼명
        fixedLengthTokenizer.setNames("one", "two");
        // fixedLength 범위 지정 - csv와 이 부분만 다름
        fixedLengthTokenizer.setColumns(new Range(1, 5), new Range(6, 10));
        BeanWrapperFieldSetMapper<TwoDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(TwoDto.class);

        dtoDefaultLineMapper.setLineTokenizer(fixedLengthTokenizer);
        dtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(dtoDefaultLineMapper);

        return flatFileItemReader;
    }

    // 라이터 - fixedlength 파일 쓰기
    @Bean
    public FlatFileItemWriter<TwoDto> fixedLengthJob2_FileWriter(Resource resource) throws Exception {
        // BeanWrapperFieldExtractor과 똑같은 내용 - 커스텀 할 수 있다는것 보여주기 위해
        // but 커스텀은 processor에서 하는 것 권장!
        BeanWrapperFieldExtractor<TwoDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"one", "two"});
        fieldExtractor.afterPropertiesSet();

        FormatterLineAggregator<TwoDto> lineAggregator = new FormatterLineAggregator<>();

        // 왼쪽, 오른쪽
        // write 할 때 포맷 설정
        lineAggregator.setFormat("%-5s###%5s");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<TwoDto>().name("fixedLengthJob2_FileWriter")
                .resource(resource)
                .lineAggregator(lineAggregator)
                .build();
    }
}