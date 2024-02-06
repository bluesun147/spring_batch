package com.example.parallel.batch;

import com.example.parallel.domain.Dept;
import com.example.parallel.domain.Dept2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;

// --job_name=jpaPageFlowJob v=1
/*
배치 병렬 실행 (parallel processing)
자동으로 배치 실행 x, 직접 정의해야 함.
 */

@RequiredArgsConstructor
@Slf4j
@Configuration
public class JpaPageFlowJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    /*
    1과 2가 parallel하게 수행 됨.
    쿼리 보면 1, 5001이 병렬적으로 테이블에 insert 됨.

    SELECT
	    count(case when dept_no >= 5000 then 1 end) as job1,
	    count(case when dept_no < 5000 then 1 end) as job2
    FROM
	    batch.dept2;

    동시에 숫자 오름
     */
    @Bean
    public Job jpaPageFlowJob_batchBuild() {

        Flow flow1 = new FlowBuilder<Flow>("flow1")
                .start(jpaPageJob_1_batchStep1())
                .build();

        Flow flow2 = new FlowBuilder<Flow>("flow2")
                .start(jpaPageJob_2_batchStep1())
                .build();

        Flow parallelStepFlow = new FlowBuilder<Flow>("parallelStepFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2)
                .build();

        return jobBuilderFactory.get("jpaPageFlowJob")
                .start(parallelStepFlow)
                .build().build();
    }

    // 병렬로 (parallel) 실행될 배치 두 세트
    // 10000건의 배치를 두 세트로 쪼개서 처리

    // 스텝

    @Bean
    public Step jpaPageJob_1_batchStep1() {
        return stepBuilderFactory.get("JpaPageFlowJob_1_Step")
                .<Dept, Dept2>chunk(chunkSize)
                .reader(jpaPageJob_1_dbItemReader())
                .processor(jpaPageJob_1_processor())
                .writer(jpaPageJob_1_dbItemWriter())
                .build();
    }

    @Bean
    public Step jpaPageJob_2_batchStep1() {
        return stepBuilderFactory.get("JpaPageFlowJob_2_Step")
                .<Dept, Dept2>chunk(chunkSize)
                .reader(jpaPageJob_2_dbItemReader())
                .processor(jpaPageJob_2_processor())
                .writer(jpaPageJob_2_dbItemWriter())
                .build();
    }

    // 리더
    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob_1_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob_1_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                // 10000건 중에 5000보다 큰 건들은 잘라서 배치 처리
                .queryString("SELECT d FROM Dept d where dept_no <= 5000 order by dept_no asc")
                .build();
    }

    // 프로세서
    @Bean
    public ItemProcessor<Dept, Dept2> jpaPageJob_1_processor() {
        return dept -> new Dept2(dept.getDeptNo(), "New2_"+dept.getDName(), "New2_"+dept.getLoc());
    }

    // 라이터
    @Bean
    public JpaItemWriter<Dept2> jpaPageJob_1_dbItemWriter() {
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    ///

    // 리더
    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob_2_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob_2_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                // 10000건 중에 5000보다 큰 건들은 잘라서 배치 처리
                .queryString("SELECT d FROM Dept d where dept_no > 5000 order by dept_no asc")
                .build();
    }

    // 프로세서
    @Bean
    public ItemProcessor<Dept, Dept2> jpaPageJob_2_processor() {
        return dept -> new Dept2(dept.getDeptNo(), "New2_"+dept.getDName(), "New2_"+dept.getLoc());
    }

    // 라이터
    @Bean
    public JpaItemWriter<Dept2> jpaPageJob_2_dbItemWriter() {
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
