package br.com.gbvbahia.threads.monitor.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class ProcessorDeleteOldTasklet implements Tasklet {

  private final ProcessorService processorService;
  
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    
    processorService.deleteOldProcess();
    
    return RepeatStatus.FINISHED;
  }

}
