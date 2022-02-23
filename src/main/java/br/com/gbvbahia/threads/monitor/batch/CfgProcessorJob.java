package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import br.com.gbvbahia.fake.environment.Environment;
import br.com.gbvbahia.threads.monitor.batch.listener.StartEndJobNotificationListener;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorApiCallerService;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CfgProcessorJob {

  public static final Integer INTEGER_OVERRIDDEN_BY_EXPRESSION = null;
  public static final Long LONG_OVERRIDDEN_BY_EXPRESSION = null;
  public static final String STRING_OVERRIDDEN_BY_EXPRESSION = null;
  
  private final JobBuilderFactory jobsFactory;
  private final StepBuilderFactory stepsFactory;
  
  private final StartEndJobNotificationListener startEndJobNotificationListener;
  
  private final ProcessorService processorService;
  private final ProcessorApiCallerService processorApiCallerService;

  @Bean
  public Job jobExecuteProcessor(Step stepExecuteProcessor) {

    return jobsFactory
        .get("jobExecuteProcessor")
        .incrementer(new RunIdIncrementer())
        .start(stepExecuteProcessor)
        .listener(startEndJobNotificationListener)
        .build();
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
            STRING_OVERRIDDEN_BY_EXPRESSION,
            STRING_OVERRIDDEN_BY_EXPRESSION))
        .processor(fakeItemProcessor())
        .writer(processorItemWriter())
        .taskExecutor(taskExecutor(INTEGER_OVERRIDDEN_BY_EXPRESSION))
        .throttleLimit(amountThreads)
        .build();
  }
  
  @Bean
  @StepScope
  public ProcessorItemReader processorItemReader(
      @Value("${app.batch.threads.amount}") Integer amountThreads,
      @Value("${app.batch.threads.factor}") Integer threadFactor,
      @Value("#{jobParameters['ITEM_READER_MODE']}") String itemReaderMode,
      @Value("#{jobParameters['ITEM_ENVIROMENT']}") String itemEnvironment) {

    return ProcessorItemReader.builder()
        .batchItemReaderMode(BatchItemReaderMode.valueOf(itemReaderMode))
        .enviroment(Environment.valueOf(itemEnvironment))
        .processorService(processorService).build();
  }

  @Bean
  public FakeItemProcessor fakeItemProcessor() {
    return FakeItemProcessor.builder().processorApiCallerService(processorApiCallerService).build();
  }

  @Bean
  public ProcessorItemWriter processorItemWriter() {
    
    return ProcessorItemWriter.builder().processorService(processorService)
        .build();
  }

  @Bean
  public ThreadPoolTaskExecutor taskExecutor(@Value("${app.batch.threads.amount}") Integer amountThreads) {
    
    log.info("ThreadPoolTaskExecutor amount threads: {}", amountThreads);
    
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(amountThreads);
    executor.setMaxPoolSize(amountThreads);
    executor.setQueueCapacity(0);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setThreadNamePrefix("process-thread");
    executor.initialize();
    return executor;
  }
}
