package br.com.gbvbahia.fake;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.javafaker.Faker;

import br.com.gbvbahia.fake.event.AmountSendingEvent;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile("!test")
@Service
@RequiredArgsConstructor
@Slf4j
public class SenderFakeService {

	private final AmountComponent amountComponent;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Value("${fake.scheduler.processor.url}")
	private String URL_DEV_LOCAL;

	@Value("${fake.scheduler.processor.endpoint}")
	private String END_POINT;

	private final TaskScheduler taskScheduler;
	private final RestTemplate restTemplate;
	private final Faker faker = Faker.instance();

	@PostConstruct
	void init() {
		requestSender();
	}

	public void requestSender() {
		log.trace("SenderFakeService.requestSender");
		
		String url = String.format("%s%s", URL_DEV_LOCAL, END_POINT);

		final int toSend = amountComponent.amountToSend();
		final long nextExecution = amountComponent.nextExecutionMilliseconds();

		applicationEventPublisher.publishEvent(AmountSendingEvent.builder().amoundSending(toSend).build());

		taskScheduler.schedule(() -> executeRestCalls(url, toSend),
				Instant.now().plus(nextExecution, ChronoUnit.MILLIS));

	}

	private void executeRestCalls(String url, final int toSend) {
		for (int idx = 0; idx < toSend; idx++) {
			Map<String, Object> request = Map.of("name", faker.name().fullName(),
					"urlToCall", faker.internet().url(),
					"dataToProcess", faker.crypto().sha512());

			ResponseEntity<ProcessorDTO> response = restTemplate.postForEntity(url, request, ProcessorDTO.class);

			log.trace("POST: {}", response.getBody());

		}
		requestSender();
	}
}
