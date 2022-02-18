package br.com.gbvbahia.threads.monitor.cfg;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestTemplateCfg {

  private static final long READ_TIMEOUT_REST_TEMPLATE = 60;
  private static final long CONNECTION_TIMEOUT_REST_TEMPLATE = 30;


  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_REST_TEMPLATE))
        .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_REST_TEMPLATE))
        .build();

    log.info("RestTemplate bean created.");
    return restTemplate;
  }
}
