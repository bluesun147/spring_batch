//package com.example.demo.withCsv;
//
//import com.example.demo.withCsv.domain.Person;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
//import org.springframework.batch.item.database.JdbcBatchItemWriter;
//import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import javax.sql.DataSource;
//
//// https://it-techtree.tistory.com/entry/creating-a-batchservice-using-springboot
//
//@Configuration
//@EnableBatchProcessing
//public class BatchConfiguration {
//
//    // job과 step에 대한 정의를 스프링 배치에 알려줘야 함.
//    // job 정의
//    // job에는 step 정의되어야 하고
//    @Autowired
//    public JobBuilderFactory jobBuilderFactory;
//
//    // step 정의
//    // step에는 reader, writer, processor 정의되어야 함
//    @Autowired
//    public StepBuilderFactory stepBuilderFactory;
//
//
//    // job에는 step이 정의되어야 하고, step은 reader, writer, processor가 정의되어야 함
//    @Bean
//    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
//        return jobBuilderFactory.get("importUserJob")
//                .incrementer(new RunIdIncrementer())
//                .listener(listener)
//                .flow(step1)
//                .end()
//                .build();
//    }
//
//    @Bean
//    public Step step1(JdbcBatchItemWriter<Person> writer) {
//        return stepBuilderFactory.get("step1")
//                // reader에서 1 row씩 데이터를 읽어 row가 chunk size 만큰 쌓이면 row를 한번에 묶어서 writer로 보냄.
//                // 즉 chunk size 만큼 트랜잭션을 실행한다는 의미.
//                // <Person, Person> 의 의미는 reader에서 읽어올 타입이 Person이고 writer에 넘겨줄 타입이 Person이라는 의미.
//                .<Person, Person> chunk(10)
//                // 파일을 읽는 행위를 reader에서,
//                .reader(reader())
//                .processor(processor())
//                // 읽ㅇ온 데이터를 저장하는 행위를 writer에서.
//                .writer(writer)
//                .build();
//
//    }
//
//    // 스프링 배치에 정의된 ItemReader 객체 활용해 데이터 읽어오는 함수
//    // 읽어온 객체를 Person 객체로 반환
//    @Bean
//    public FlatFileItemReader<Person> reader() {
//        return new FlatFileItemReaderBuilder<Person>()
//                .name("personItemReader")
//                .resource(new ClassPathResource("withCsv/sample-data.csv"))
//                .delimited()
//                .names(new String[]{"firstName", "lastName"})
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){{
//                    setTargetType(Person.class);
//                }})
//                .build();
//    }
//
//    // 스프링 배치에 데이터 변환 담당할 클래스 알려줘야 함.
//    @Bean
//    public PersonItemProcessor processor() {
//        return new PersonItemProcessor();
//    }
//
//    // 데이터 처리 로직 구현. db에 저장하는 용도
//    @Bean
//    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<Person>()
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
//                .dataSource(dataSource)
//                .build();
//    }
//}