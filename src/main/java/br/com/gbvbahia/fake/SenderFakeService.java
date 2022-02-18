package br.com.gbvbahia.fake;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.github.javafaker.Faker;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile("dev")
@Service
@RequiredArgsConstructor
@Slf4j
public class SenderFakeService {

  private ApplicationEventPublisher applicationEventPublisher;
  
  @Value("${fake.scheduler.processor.url}")
  private String URL_DEV_LOCAL;
  
  @Value("${fake.scheduler.processor.endpoint}")
  private String END_POINT;
  
  private final RestTemplate restTemplate;
  private final Faker faker = Faker.instance();

  @Scheduled(fixedRateString = "${fake.scheduler.processor.rate}",
      initialDelayString = "${fake.scheduler.processor.delay}")
  public void requestSender() throws Exception {

    String url = String.format("%s%s", URL_DEV_LOCAL, END_POINT);
    
    Map<String, Object> request = Map.of("name", faker.name().fullName(),
        "urlToCall", faker.internet().url(), 
        "dataToProcess", faker.crypto().sha512());
    
    ResponseEntity<ProcessorDTO> response = restTemplate.postForEntity(url, request, ProcessorDTO.class);
    
    log.trace("POST: {}", response.getBody());
    
    //TODO
    applicationEventPublisher.publishEvent(response);
  }
}
