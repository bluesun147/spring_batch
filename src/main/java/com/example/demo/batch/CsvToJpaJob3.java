package com.example.demo.batch;
import com.example.demo.domain.Two;
import com.example.demo.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;

// 스프링 배치 with 도커
// 파라미터 : --job.name=csvToJpaJob3 inFileName=INFILES/csvToJpaJob3.txt outFileName=1 version=2
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CsvToJpaJob3 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private static final int chunkSize = 5;

    // job name 정의된 builder
    @Bean
    public Job csvToJpaJob3_batchBuild() throws Exception {
        return jobBuilderFactory.get("csvToJpaJob3")
                .start(csvToJpaJob3_batchStep1())
                .build();
    }

    // 스텝
    @Bean
    public Step csvToJpaJob3_batchStep1() throws Exception {
        return stepBuilderFactory.get("csvToJpaJob3_batchStep1")
                // <I, O>
                .<TwoDto, Two>chunk(chunkSize)
                .reader(csvToJpaJob3_FileReader(null))
                .processor(csvToJpaJob3_processor())
                .writer(csvToJpaJob3_dbItemWriter())
                .build();
    }

    // 리더
    @Bean
    @StepScope
    public FlatFileItemReader<TwoDto> csvToJpaJob3_FileReader(@Value("#{jobParameters[inFileName]}") String inFileName) {
        return new FlatFileItemReaderBuilder<TwoDto>()
                .name("csvToJpaJob3_FileReader")
                // 프로젝트 제일 상위 경로 밑
                .resource(new FileSystemResource(inFileName))
                .delimited().delimiter(":")
                .names("one", "two")
                .targetType(TwoDto.class)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy(){
                    @Override
                    public String postProcess(String record){
                        log.debug("policy: " + record);
                        // 파서 대상이 아니면 무시
                        if(record.indexOf(":") == -1){
                            return null;
                        }
                        return record.trim();
                    }
                })
                .build();

    }

    // 프로세서
    @Bean
    public ItemProcessor<TwoDto, Two> csvToJpaJob3_processor() {
        // dto를 Two 도메인으로 만들어서 태이블에 집어넣기
        return twoDto -> new Two(twoDto.getOne(), twoDto.getTwo());
    }

    // 라이터
    @Bean
    public JpaItemWriter<Two> csvToJpaJob3_dbItemWriter() {
        JpaItemWriter<Two> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
