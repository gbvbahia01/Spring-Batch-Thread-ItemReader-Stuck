package br.com.gbvbahia.threads.monitor.batch;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.threads.monitor.event.BatchTaskExecutorInfoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class ThreadMonitoringScheduler {

  private final ThreadPoolTaskExecutor taskExecutor;
  private final ApplicationEventPublisher applicationEventPublisher;
  
  @Scheduled(fixedRateString = "${app.scheduler.batch.threads.rate}",
      initialDelayString = "${app.scheduler.batch.threads.delay}")
  public void monitoringThreads() throws Exception {
    
    BatchTaskExecutorInfoEvent event = BatchTaskExecutorInfoEvent.builder()
        .activeCount(taskExecutor.getActiveCount())
        .corePoolSize(taskExecutor.getCorePoolSize())
        .maxPoolSize(taskExecutor.getMaxPoolSize())
        .poolSize(taskExecutor.getPoolSize()).build();

    log.trace("Task info event: {}", event);

    applicationEventPublisher.publishEvent(event);
  }
}
