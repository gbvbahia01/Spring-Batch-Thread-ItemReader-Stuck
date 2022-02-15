package br.com.gbvbahia.threads.monitor.batch;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProcessJobCfg {

  private final JobBuilderFactory jobsFactory;
  private final StepBuilderFactory stepsFactory;
  
  private final ProcessorService processorService;
  
  
  @Bean
  @StepScope
  public ProcessorItemReader processorItemReader(
      @Value("${app.batch.threads.amount}") Integer amountThreads,
      @Value("${app.batch.threads.factor}") Integer threadFactor,
      @Value("#{jobParameters['itemReaderMode']}") String itemReaderMode) {
   
    return ProcessorItemReader.builder()
        .amountThreads(amountThreads)
        .threadFactor(threadFactor)
        .batchItemReaderMode(BatchItemReaderMode.valueOf(itemReaderMode))
        .processorService(processorService)
        .build();
  }
  
  @Bean
  public FakeItemProcessor fakeItemProcessor() {
    return new FakeItemProcessor();
  }
  
  
}
