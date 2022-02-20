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
public class ProcessorIdleJobScheduler {

  private final ProcessorService processorService;

  @Scheduled(fixedRateString = "${app.scheduler.processor.idle.rate}")
  public void idle() {
    log.trace("Processor Idle Started");

    processorService.releaseIdleProcess();;

    log.trace("Processor Idle Finished");
  }

}
