package com.example.demo.batch;

// JpaPageJob - db 가져오는 배치 종류 중 한가지. 제일 많이 씀
// JpaPagingItemReader, JpaItemWriter 등등
// https://www.youtube.com/watch?v=wy99cPHlMlA

import com.example.demo.domain.Dept;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPageJob1 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    // 잘라서 배치 처리하기 위해서
    private int chunkSize = 10;

    @Bean
    // TaskletJob에서 configuration으로 등록되었기 때문에 이름 같으면 에러 발생
    public Job jpaPageJob1_batchBuild() {
        // jpaPageJob1이라는 배치 만들고
        return jobBuilderFactory.get("jpaPageJob1")
                .start(jpaPageJob1_step1()).build();
    }

    @Bean
    public Step jpaPageJob1_step1() {
        return stepBuilderFactory.get("jpaPageJob1_step1")
                // <in, out> (리터, 라이터)
                .<Dept, Dept>chunk(chunkSize)
                // spring batch 구조 그림의 리더와 라이터
                .reader(jpaPageJob1_dbItemReader())
                .writer(jpaPageJob1_printItemWriter())
                .build();
    }

    // 페이지 리더 아이템 생성됨 - 리딩하는 아이템
    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob1_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob1_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                // 가져온 뒤 오름차순 정렬
                // 이거 지금 테스트에서 100개 save하는거 동작 안해서 실행 안되는 것!!
                .queryString("SELECT d FROM Dept d ORDER BY dept_no ASC")
                .build();
    }

    // 라이터
    // 여기서는 실제 db에 넣지 않고 로그만
    public ItemWriter<Dept> jpaPageJob1_printItemWriter() {
        return list -> {
            for (Dept dept : list) {
                log.debug(dept.toString());
            }
        };
    }
}
