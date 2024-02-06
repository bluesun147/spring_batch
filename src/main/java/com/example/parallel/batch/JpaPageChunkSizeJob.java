package com.example.parallel.batch;

import com.example.parallel.domain.Dept;
import com.example.parallel.domain.Dept2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

// --job_name=jpaPageChunkSizeJob v=1 chunkSize=1000

// chunk size 10일때 15초 (batch_job_execution 테이블 startTime, endTime)
// chunk size 100일때 12초
// chunk size 1000일때 8초
// chunk size 10000일때 7초

// 로그로 인해 느려질 수 있음

@RequiredArgsConstructor
@Slf4j
@Configuration
public class JpaPageChunkSizeJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    @Bean
    public Job jpaPageJob_batchBuild() {
        return jobBuilderFactory.get("jpaPageChunkSizeJob")
                .start(jpaPageJob_batchStep1(0))
                .build();
    }

    @Bean
    // jobParameters 들어갈 경우에 @JobScope 사용해야 함
    @JobScope
    public Step jpaPageJob_batchStep1(@Value("#{jobParameters[chunkSize]}") int chunkSize) {
        return stepBuilderFactory.get("JpaPageJob1_Step")
                .<Dept, Dept2>chunk(chunkSize)
                .reader(jpaPageJob_dbItemReader(0))
                .processor(jpaPageJob_processor())
                .writer(jpaPageJob_dbItemWriter())
                .build();
    }

    // 리더
    @Bean
    @JobScope
    public JpaPagingItemReader<Dept> jpaPageJob_dbItemReader(@Value("#{jobParameters[chunkSize]}") int chunkSize) {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("JpaPageJob1_Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT d FROM Dept d ORDER BY dept_no asc")
                .build();
    }

    // 프로세서
    @Bean
    public ItemProcessor<Dept, Dept2>jpaPageJob_processor() {
        return  dept -> new Dept2(dept.getDeptNo(), "NEW!_" + dept.getDName(), "NEW!_" + dept.getLoc());
    }

    // 라이터
    @Bean
    public JpaItemWriter<Dept2> jpaPageJob_dbItemWriter() {
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}