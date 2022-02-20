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
public class ProcessorCounterScheduler {

  private final ProcessorService processorService;
  
  @Scheduled(fixedRateString = "${app.scheduler.processor.counter.rate}")
  public void counter() {
    log.trace("Processor Counter Started");
    
    processorService.generateProcessorCounterEvent();
    
    log.trace("Processor Counter Finished");
  }
}
