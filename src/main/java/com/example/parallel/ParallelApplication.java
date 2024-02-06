package com.example.parallel;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;

@EnableBatchProcessing
@SpringBootApplication
public class ParallelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParallelApplication.class, args);
	}

}
