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
public class ProcessorOldJobScheduler {

  private final ProcessorService processorService;

  @Scheduled(fixedRateString = "${app.scheduler.processor.old.rate}")
  public void idle() {
    log.trace("Processor Old Started");

    processorService.deleteOldProcess();;

    log.trace("Processor Old Finished");
  }

}
