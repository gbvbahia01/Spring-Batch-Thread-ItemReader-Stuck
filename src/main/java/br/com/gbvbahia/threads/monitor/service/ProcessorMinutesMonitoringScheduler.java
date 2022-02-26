package br.com.gbvbahia.threads.monitor.service;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class ProcessorMinutesMonitoringScheduler {

  private final ProcessorService processorService;
  
  @Scheduled(fixedRateString = "${app.scheduler.processor.minutes.rate}")
  public void counter() {
    log.trace("Processor Minutes Monitoring Started");
    
    processorService.maxDiferenceBetweenCreatedAndFinishedEvent();
    
    log.trace("Processor Minutes Monitoring Finished");
  }
}
