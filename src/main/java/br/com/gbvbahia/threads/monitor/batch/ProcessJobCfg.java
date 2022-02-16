package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorApiCallerService;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ProcessJobCfg {

  public static final Integer INTEGER_OVERRIDDEN_BY_EXPRESSION = null;
  public static final Long LONG_OVERRIDDEN_BY_EXPRESSION = null;
  public static final String STRING_OVERRIDDEN_BY_EXPRESSION = null;
  
  private final JobBuilderFactory jobsFactory;
  private final StepBuilderFactory stepsFactory;

  private final ProcessorService processorService;
  private final ProcessorApiCallerService processorApiCallerService;

  @Bean
  public Job jobExecuteProcessor(Step stepExecuteProcessor) {

    return jobsFactory
        .get("jobExecuteProcessor")
        .incrementer(new RunIdIncrementer())
        .start(stepExecuteProcessor)
        .build();
  }
  
  @Bean
  public Step stepExecuteProcessor(
      @Value("${app.batch.threads.chunks}") Integer chunks,
      @Value("${app.batch.threads.amount}") Integer amountThreads) {

    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
    simpleAsyncTaskExecutor.setConcurrencyLimit(amountThreads);

    return this.stepsFactory
        .get("stepExecuteProcessor")
        .<Optional<Processor>, Optional<Processor>>chunk(chunks)
        .reader(processorItemReader(INTEGER_OVERRIDDEN_BY_EXPRESSION,
            INTEGER_OVERRIDDEN_BY_EXPRESSION,
            STRING_OVERRIDDEN_BY_EXPRESSION))
        .processor(fakeItemProcessor())
        .writer(processorItemWriter())
        .taskExecutor(simpleAsyncTaskExecutor)
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
    
    return ProcessorItemWriter.builder().processorService(processorService).build();
  }

}
