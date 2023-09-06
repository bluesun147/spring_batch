//package com.example.demo.withCsv;
//
//import com.example.demo.withCsv.domain.Person;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.listener.JobExecutionListenerSupport;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//// job 종료 후 후속 처리 러스너
//// job 정상 종료 후에 db에 저장되었는지 확인
//@Component
//public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
//    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
//    private final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
//            log.info("!!! JOB 종료! 결과를 확인하자");
//
//            jdbcTemplate.query("SELECT first_name, last_name FROM people",
//                    (rs, row) -> new Person(
//                            rs.getString(1),
//                            rs.getString(2))
//            ).forEach(people -> log.info("Found <" + people + "> in the database."));
//        }
//    }
//}
