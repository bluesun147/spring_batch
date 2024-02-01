package com.example.demo.batch;

import com.example.demo.domain.Dept;
import com.example.demo.domain.Dept2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPageJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    // 잘라서 배치 처리하기 위해서
    private int chunkSize = 10;

    @Bean
    // TaskletJob에서 configuration으로 등록되었기 때문에 이름 같으면 에러 발생
    public Job jpaPageJob2_batchBuild() {
        // jpaPageJob1이라는 배치 만들고
        return jobBuilderFactory.get("jpaPageJob2")
                .start(jpaPageJob2_step1()).build();
    }

    @Bean
    public Step jpaPageJob2_step1() {
        return stepBuilderFactory.get("jpaPageJob2_step1")
                // <in, out> (리터, 라이터)
                .<Dept, Dept2>chunk(chunkSize)
                // spring batch 구조 그림의 리더, 프로세서, 라이터
                .reader(jpaPageJob2_dbItemReader())
                // 가공 과정
                .processor(jpaPageJob2_processor())
                .writer(jpaPageJob2_dbItemWriter())
                .build();
    }

    // Dept 읽어서 Dept2로 가공해서 저장
    private ItemProcessor<Dept, Dept2> jpaPageJob2_processor() {
        return dept -> {
            return new Dept2(dept.getDeptNo(), "NEW_"+dept.getDName(), "NEW_"+dept.getLoc());
        };
    }

    // 페이지 리더 아이템 생성됨 - 리딩하는 아이템
    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob2_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob2_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                // 가져온 뒤 오름차순 정렬
                // 이거 지금 테스트에서 100개 save하는거 동작 안해서 실행 안되는 것!!
                .queryString("SELECT d FROM Dept d ORDER BY dept_no ASC")
                .build();
    }

    // 라이터 - dept2에 저장
    public JpaItemWriter<Dept2> jpaPageJob2_dbItemWriter() {
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
