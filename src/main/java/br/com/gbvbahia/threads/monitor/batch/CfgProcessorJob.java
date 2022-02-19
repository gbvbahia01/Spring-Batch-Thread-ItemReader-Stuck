package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import br.com.gbvbahia.threads.monitor.batch.listener.StartEndJobNotificationListener;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorApiCallerService;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CfgProcessorJob {

  public static final Integer INTEGER_OVERRIDDEN_BY_EXPRESSION = null;
  public static final Long LONG_OVERRIDDEN_BY_EXPRESSION = null;
  public static final String STRING_OVERRIDDEN_BY_EXPRESSION = null;
  
  private final JobBuilderFactory jobsFactory;
  private final StepBuilderFactory stepsFactory;
  private final ApplicationEventPublisher applicationEventPublisher;
  
  private final StartEndJobNotificationListener startEndJobNotificationListener;
  
  private final ProcessorService processorService;
  private final ProcessorApiCallerService processorApiCallerService;

  @Bean
  public Job jobExecuteProcessor(Step stepReleaseIdleProcess,
      Step stepDeleteOldProcess,
      Step stepExecuteProcessor) {

    return jobsFactory
        .get("jobExecuteProcessor")
        .incrementer(new RunIdIncrementer())
        .start(stepReleaseIdleProcess)
        .next(stepDeleteOldProcess)
        .next(stepExecuteProcessor)
        .listener(startEndJobNotificationListener)
        .build();
  }
  
  @Bean
  public Step stepReleaseIdleProcess() {
    return this.stepsFactory.get("stepIdleProcess")
        .tasklet(processorReleaseIdleTasklet())
        .build();
  }
  
  @Bean
  public ProcessorReleaseIdleTasklet processorReleaseIdleTasklet() {
    return ProcessorReleaseIdleTasklet.builder().processorService(processorService).build();
  }
  
  @Bean
  public Step stepDeleteOldProcess() {
    return this.stepsFactory.get("stepDeleteOldProcess")
        .tasklet(processorDeleteOldTasklet())
        .build();
  }
  
  @Bean
  public ProcessorDeleteOldTasklet processorDeleteOldTasklet() {
    return ProcessorDeleteOldTasklet.builder().processorService(processorService).build();
  }
  
  @Bean
  public Step stepExecuteProcessor(
      @Value("${app.batch.threads.chunks}") Integer chunks,
      @Value("${app.batch.threads.amount}") Integer amountThreads) {

    return this.stepsFactory
        .get("stepExecuteProcessor")
        .<Optional<Processor>, Optional<Processor>>chunk(chunks)
        .reader(processorItemReader(INTEGER_OVERRIDDEN_BY_EXPRESSION,
            INTEGER_OVERRIDDEN_BY_EXPRESSION,
            STRING_OVERRIDDEN_BY_EXPRESSION))
        .processor(fakeItemProcessor())
        .writer(processorItemWriter())
        .taskExecutor(taskExecutor(INTEGER_OVERRIDDEN_BY_EXPRESSION))
        .build();
  }
  
  @Bean
  @StepScope
  public ProcessorItemReader processorItemReader(
      @Value("${app.batch.threads.amount}") Integer amountThreads,
      @Value("${app.batch.threads.factor}") Integer threadFactor,
      @Value("#{jobParameters['ITEM_READER_MODE']}") String itemReaderMode) {

    return ProcessorItemReader.builder().amountThreads(amountThreads).threadFactor(threadFactor)
        .batchItemReaderMode(BatchItemReaderMode.valueOf(itemReaderMode))
        .processorService(processorService).build();
  }

  @Bean
  public FakeItemProcessor fakeItemProcessor() {
    return FakeItemProcessor.builder().processorApiCallerService(processorApiCallerService).build();
  }

  @Bean
  public ProcessorItemWriter processorItemWriter() {
    
    return ProcessorItemWriter.builder().processorService(processorService)
        .taskExecutor(taskExecutor(INTEGER_OVERRIDDEN_BY_EXPRESSION))
        .applicationEventPublisher(applicationEventPublisher)
        .build();
  }

  @Bean
  public ThreadPoolTaskExecutor taskExecutor(@Value("${app.batch.threads.amount}") Integer amountThreads) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(amountThreads);
    executor.setMaxPoolSize(amountThreads);
    executor.setThreadNamePrefix("process-thread");
    executor.initialize();
    return executor;
  }
}
