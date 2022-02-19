package br.com.gbvbahia.threads.monitor.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartEndJobNotificationListener implements JobExecutionListener {


  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.trace("Job:{}-{} START", jobExecution.getJobId(),
        jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.trace("Job:{}-{} END", jobExecution.getJobId(),
        jobExecution.getJobInstance().getJobName());
  }
}
