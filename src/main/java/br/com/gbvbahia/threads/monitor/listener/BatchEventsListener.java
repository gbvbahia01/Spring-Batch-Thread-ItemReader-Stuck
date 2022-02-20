package br.com.gbvbahia.threads.monitor.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.threads.monitor.dto.BatchModeController;
import br.com.gbvbahia.threads.monitor.dto.TaskExecutionDTO;
import br.com.gbvbahia.threads.monitor.event.BatchReadModeChangedEvent;
import br.com.gbvbahia.threads.monitor.event.BatchTaskExecutorInfoEvent;
import br.com.gbvbahia.threads.monitor.event.JobStartEndEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchEventsListener {

  @Value("${app.batch.threads.amount}")
  private Integer amountThreads;
  
  private final SimpMessagingTemplate simpMessagingTemplate;
  
  @EventListener
  public void batchTaskExecutorInfoListener(BatchTaskExecutorInfoEvent event) {
    
    final double percent = Double.valueOf(event.getActiveCount()) / amountThreads;
    
    TaskExecutionDTO dto = TaskExecutionDTO.builder()
    .activeCount(event.getActiveCount())
    .corePoolSize(event.getCorePoolSize())
    .maxPoolSize(event.getMaxPoolSize())
    .poolSize(event.getPoolSize())
    .ymlAmountThreads(amountThreads)
    .percent((int)(percent * 100))
    .color(percent >= 0.95 ? "bg-success" : percent >= 0.75 ? "bg-warning" : "bg-danger")
    .build();
    
    simpMessagingTemplate.convertAndSend("/topic/taskExecutorInfo",  dto);
  }
  
  @EventListener
  public void JobStartEndListener(JobStartEndEvent event) {
    
    simpMessagingTemplate.convertAndSend("/topic/jobStartEnd",  event);
  }
  
  @EventListener
  public void BatchReadModeChangedListener(BatchReadModeChangedEvent event) {
    if (event.isReadModeChanged()) {
      simpMessagingTemplate.convertAndSend("/topic/readMode",  BatchModeController.INSTANCE.getBatchMode());
    }
  }
}
