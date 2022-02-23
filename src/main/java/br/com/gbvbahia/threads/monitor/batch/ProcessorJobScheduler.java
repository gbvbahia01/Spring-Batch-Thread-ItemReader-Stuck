package br.com.gbvbahia.threads.monitor.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.threads.monitor.dto.BatchModeController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class ProcessorJobScheduler {

  private final JobLauncher jobLauncher;
  private final Job jobExecuteProcessor;
  
  @Scheduled(fixedRateString = "${app.scheduler.batch.rate}",
             initialDelayString = "${app.scheduler.batch.delay}")
  public void processStarter() throws Exception {
    log.trace("Init Job Processor");
    
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addLong("JOB_TIMESTAMP", System.nanoTime())
            .addString("ITEM_READER_MODE", BatchModeController.INSTANCE.getBatchMode().name())
            .addString("ITEM_ENVIROMENT", EnvironmentCurrentController.INSTANCE.getCurrent().name())
            .toJobParameters();
    
    JobExecution jobResult = jobLauncher.run(jobExecuteProcessor, jobParameters);
    
    log.trace("End Job Processor with status: {}", jobResult.getStatus());
  }
  
  
}
