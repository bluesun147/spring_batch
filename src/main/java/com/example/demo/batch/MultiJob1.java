package com.example.demo.batch;

import com.example.demo.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;

import javax.persistence.EntityManagerFactory;

// 파라미터 : --job.name=multiJob1 inFileName=multiJob1.txt outFileName=1 version=1
/*
@JobScope, @StepScope : 빈 범위 지정 어노테이션
스프링 빈 -> 싱글톤 -> 애플리케이션 생성 시 계속 사용됨
but 배치 작업에서는 각 step, job 따라 특정 빈 인스턴스 필요
StepScope/JobScope 사용 시 step/job 범위 내에서만 빈 생성되고 끝나면 소멸
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MultiJob1 {

    private final ResourceLoader resourceLoader;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private static final int chunkSize = 5;

    // 잡 등록
    @Bean
    public Job multiJob1_batchBuild() {
        return jobBuilderFactory.get("multiJob1")
                .start(multiJob1_batchStep1(null))
                .build();
    }

    @Bean
    @JobScope
    public Step multiJob1_batchStep1(@Value("#{jobParameters[version]}")String version) {
        log.debug("-----");
        log.debug(version);
        log.debug("-----");

        return stepBuilderFactory.get("multiJob1_batchStep1")
                .<TwoDto, TwoDto>chunk(chunkSize)
                .reader(multiJob1_Reader(null))
                .processor(multiJob1_Processor(null))
                .writer(multiJob1_Writer(null))
                .build();
    }

    // 리더
    @Bean
    // 파라미터 잡기 위해
    @StepScope
    public FlatFileItemReader<TwoDto> multiJob1_Reader(@Value("#{jobParameters[inFileName]}")String inFileName) {
        return new FlatFileItemReaderBuilder<TwoDto>()
                .name("multiJob1_Reader")
                // resources 폴더 밑에 경로
                .resource(new ClassPathResource("sample/" + inFileName))
                // 구분자
                .delimited().delimiter(":")
                .names("one", "two")
                .targetType(TwoDto.class)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy(){
                    @Override
                    public String postProcess(String record){
                        log.debug("policy: " + record);
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
    @StepScope
    public ItemProcessor<TwoDto, TwoDto> multiJob1_Processor(@Value("#{jobParameters[version]}")String version) {
        log.debug("processor: " + version);
        return twoDto -> new TwoDto(twoDto.getOne(), twoDto.getTwo());
    }

    // 라이터
    @Bean
    @StepScope
    public FlatFileItemWriter<TwoDto> multiJob1_Writer(@Value("#{jobParameters[outFileName]}")String outFileName) {
        return new FlatFileItemWriterBuilder<TwoDto>()
                .name("multiJob1_Writer")
                // 제일 상위 폴더 (spring_batch) 밑 경로
                .resource(new FileSystemResource("sample/" + outFileName))
                // item이 dto
                .lineAggregator(item -> {
                    return item.getOne() + " --- " + item.getTwo();
                })
                .build();

    }
}
