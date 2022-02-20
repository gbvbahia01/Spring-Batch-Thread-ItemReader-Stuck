package br.com.gbvbahia.threads.monitor.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.threads.monitor.event.JobStartEndEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartEndJobNotificationListener implements JobExecutionListener {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.trace("Job:{}-{} START", jobExecution.getJobId(),
        jobExecution.getJobInstance().getJobName());

    applicationEventPublisher.publishEvent(
        JobStartEndEvent.builder().startJob(Boolean.TRUE).jobId(jobExecution.getJobId())
            .jobName(jobExecution.getJobInstance().getJobName()).build());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.trace("Job:{}-{} END", jobExecution.getJobId(), jobExecution.getJobInstance().getJobName());
    
    applicationEventPublisher.publishEvent(
        JobStartEndEvent.builder().startJob(Boolean.FALSE).jobId(jobExecution.getJobId())
            .jobName(jobExecution.getJobInstance().getJobName()).build());
  }
}
