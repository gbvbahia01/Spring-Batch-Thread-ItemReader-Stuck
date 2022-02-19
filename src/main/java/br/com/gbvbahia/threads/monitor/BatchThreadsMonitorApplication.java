package br.com.gbvbahia.threads.monitor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
@EnableBatchProcessing
@EnableWebSecurity
@EnableWebSocketMessageBroker
@EnableWebMvc
@ComponentScan("br.com.gbvbahia")
public class BatchThreadsMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchThreadsMonitorApplication.class, args);
	}

}
