package com.example.multidb.batch;

import com.example.multidb.db1.Dept1;
import com.example.multidb.db2.Dept2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

// 다중 데이터베이스 환경
// multidb1의 dept1에서 multidb2의 dept2로
// 파라미터 : --job.name=multipleDBJob version=2

@Slf4j
@Configuration
public class MultipleDBJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // 메인 config DbConfig1에서 가져오겠다
    @Resource(name = "entityManagerFactory1")
    private EntityManagerFactory entityManagerFactory1;

    @Resource(name = "dataSource2")
    private DataSource dataSource2;

    private int chunkSize = 10;

    @Bean
    public Job jpaPageJob2_batchBuild() {
        return jobBuilderFactory.get("multipleDBJob")
                .start(jpaPageJob2_batchStep1())
                .build();
    }

    @Bean
    @JobScope
    public Step jpaPageJob2_batchStep1() {
        return stepBuilderFactory.get("JpaPageJob1_Step")
                .<Dept1, Dept2>chunk(chunkSize)
                .reader(jpaPageJob2_dbItemReader())
                .processor(jpaPageJob2_processor())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    // 리더
    @Bean
    @StepScope
    public JpaPagingItemReader<Dept1> jpaPageJob2_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept1>()
                .name("JpaPageJob2_Reader")
                .entityManagerFactory(entityManagerFactory1)
                .pageSize(chunkSize)
                .queryString("SELECT d FROM Dept1 d order by deptno asc")
                .build();
    }

    // 프로세서
    @Bean
    @StepScope
    public ItemProcessor<Dept1, Dept2> jpaPageJob2_processor() {
        return Dept1 -> {
            // 데이터 커스텀
            // 짝수일때만 저장
            if (Dept1.getDeptno() % 2 == 0) {
                return new Dept2(Dept1.getDeptno(), "NEW_" + Dept1.getDname(), "NEW_" + Dept1.getLoc());
            } else {
                return null;
            }
        };
    }

    // 라이터
    @Bean
    public JdbcBatchItemWriter<Dept2> jdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Dept2>()
                .dataSource(dataSource2)
                .sql("insert into Dept2(deptno, dname, loc) values (:deptno, :dname, :loc)")
                .beanMapped()
                .build();
    }
}