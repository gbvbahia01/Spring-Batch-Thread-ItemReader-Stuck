package br.com.gbvbahia.threads.monitor.service;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessorApiCallerService {

	private Faker faker = Faker.instance();

	public String requestDataToProcess(String url) {
		
		try {
			
			Thread.sleep(faker.number().numberBetween(250, 350));
			
		} catch (Exception e) {
			log.error("Error on thread sleeping", e);
		}
		
		return faker.job().title();

	}

}
