package com.example.demo.batch;
// https://www.youtube.com/watch?v=zW-lrhOV7PQ&list=PLogzC_RPf25HRSG9aO7qKrwbT-EecUMMR&index=2
import com.example.demo.domain.Dept;
import com.example.demo.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.xmlbeans.impl.jam.provider.ResourcePath;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CsvToJpaJob2 {

    // 멀티 파일 위해서
    private final ResourceLoader resourceLoader;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private static final int chunkSize = 10;

    // job name 정의된 builder
    @Bean
    public Job csvToJpaJob2_batchBuild() throws Exception {
        return jobBuilderFactory.get("csvToJpaJob2")
                .start(csvToJpaJob2_batchStep1())
                .build();
    }

    // 스텝
    @Bean
    public Step csvToJpaJob2_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvToJpaJob2_batchStep1")
                // <I, O>
                .<TwoDto, Dept>chunk(chunkSize)
                .reader(csvToJpaJob2_FileReader())
                .processor(csvToJpaJob2_processor())
                .writer(csvToJpaJob2_dbItemWriter())
                .faultTolerant()
                // 해당 에러나면 스킵
                .skip(FlatFileParseException.class)
                // 100개 이상 나면 에러로 종료
                .skipLimit(100)
                .build();
    }

    // 리더
    @Bean
    public MultiResourceItemReader<TwoDto> csvToJpaJob2_FileReader() {
        MultiResourceItemReader<TwoDto> twoDtoMultiResourceItemReader = new MultiResourceItemReader<>();


        try {
            twoDtoMultiResourceItemReader.setResources(
                    ResourcePatternUtils.getResourcePatternResolver(this.resourceLoader).getResources(
                            "classpath:sample/csvToJpaJobs2/*.txt"
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 어떻게 읽을 지 메소드로 빼서 지정
        twoDtoMultiResourceItemReader.setDelegate(multiFileItemReader());

        return twoDtoMultiResourceItemReader;
    }

    @Bean
    public FlatFileItemReader<TwoDto> multiFileItemReader() {
        FlatFileItemReader<TwoDto> flatFileItemReader = new FlatFileItemReader<>();

        flatFileItemReader.setLineMapper((line, lineNumber) -> {
            String[] lines = line.split("#");
            return new TwoDto(lines[0], lines[1]);
        });

        return flatFileItemReader;
    }

    // 프로세서
    @Bean
    public ItemProcessor<TwoDto, Dept> csvToJpaJob2_processor() {
        return twoDto -> new Dept(Integer.parseInt(twoDto.getOne()), twoDto.getTwo(), "기타");
    }

    // 라이터
    @Bean
    public JpaItemWriter<Dept> csvToJpaJob2_dbItemWriter() {
        JpaItemWriter<Dept> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
