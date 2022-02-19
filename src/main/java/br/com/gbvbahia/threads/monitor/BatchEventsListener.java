package br.com.gbvbahia.threads.monitor;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.threads.monitor.event.BatchTaskExecutorInfoEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchEventsListener {

  private final SimpMessagingTemplate simpMessagingTemplate;
  
  @EventListener
  public void BatchTaskExecutorInfoListener(BatchTaskExecutorInfoEvent event) {
    
    simpMessagingTemplate.convertAndSend("/topic/taskExecutorInfo",  event);
  }
}
