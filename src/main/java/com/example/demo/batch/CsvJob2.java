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
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

// csv 파일 쓰기

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final int chunkSize = 5;

    // job
    @Bean
    public Job csvJob2_batchBuild() throws Exception {
        return jobBuilderFactory.get("csvJob2")
                .start(csvJob2_batchStep1())
                .build();
    }

    // step
    @Bean
    public Step csvJob2_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvJob2_batchStep1")
                .<TwoDto, TwoDto>chunk(chunkSize)
                .reader(csvJob2_FileReader())
                // .processor()
                .writer(csvJob2_FileWriter(new FileSystemResource("output/csvJob2_output.csv")))
                .build();
    }

    // 리더 - csv 파일 읽기
    @Bean
    public FlatFileItemReader<TwoDto> csvJob2_FileReader() {
        FlatFileItemReader<TwoDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/sample/csvJob2_input.csv"));
        // csv 파일 맨 윗줄의 컬럼명 빼고 데이터만 읽기 위해
        flatFileItemReader.setLinesToSkip(1);
        DefaultLineMapper<TwoDto> dtoDefaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        // 컬럼명
        delimitedLineTokenizer.setNames("one", "two");
        // 구분자
        delimitedLineTokenizer.setDelimiter(":");

        BeanWrapperFieldSetMapper<TwoDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(TwoDto.class);

        dtoDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        dtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(dtoDefaultLineMapper);

        return flatFileItemReader;
    }

    // 라이터 - csv 파일 쓰기
    @Bean
    public FlatFileItemWriter<TwoDto> csvJob2_FileWriter(Resource resource) throws Exception {
        // BeanWrapperFieldExtractor과 똑같은 내용 - 커스텀 할 수 있다는것 보여주기 위해
        // but 커스텀은 processor에서 하는 것 권장!
        CustomBeanWrapperFieldExtractor<TwoDto> customBeanWrapperFieldExtractor = new CustomBeanWrapperFieldExtractor<>();
        customBeanWrapperFieldExtractor.setNames(new String[]{"one", "two"});
        customBeanWrapperFieldExtractor.afterPropertiesSet();

        // 구분자
        DelimitedLineAggregator<TwoDto> dtoDelimitedLineAggregator = new DelimitedLineAggregator<>();
        dtoDelimitedLineAggregator.setDelimiter("@");
        dtoDelimitedLineAggregator.setFieldExtractor(customBeanWrapperFieldExtractor);

        return new FlatFileItemWriterBuilder<TwoDto>().name("csvJob2_FileWriter")
                .resource(resource)
                .lineAggregator(dtoDelimitedLineAggregator)
                .build();
    }
}