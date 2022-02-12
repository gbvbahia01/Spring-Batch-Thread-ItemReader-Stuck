package br.com.gbvbahia.threads.monitor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
@EnableBatchProcessing
public class BatchThreadsMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchThreadsMonitorApplication.class, args);
	}

}
