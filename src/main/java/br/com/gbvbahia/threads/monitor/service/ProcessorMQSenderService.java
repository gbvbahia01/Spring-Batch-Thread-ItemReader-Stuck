package br.com.gbvbahia.threads.monitor.service;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessorMQSenderService {

	private Faker faker = Faker.instance();

	public void sendDataToMQ(String data) {

		try {

			Thread.sleep(faker.number().numberBetween(150, 350));

		} catch (Exception e) {
			log.error("Error on thread sleeping", e);
		}

	}

}
