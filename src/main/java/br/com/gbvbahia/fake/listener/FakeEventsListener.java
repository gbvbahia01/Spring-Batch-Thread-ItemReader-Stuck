package br.com.gbvbahia.fake.listener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.fake.event.AmountSendingEvent;
import br.com.gbvbahia.fake.event.EnvironmentChangedEvent;
import br.com.gbvbahia.threads.monitor.event.CurrentJobShouldStopEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FakeEventsListener {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final ApplicationEventPublisher applicationEventPublisher;
  
  @EventListener
  public void environmentChangedListener(EnvironmentChangedEvent event) {
    if (event.isEnvironmentChanged()) {
      simpMessagingTemplate.convertAndSend("/topic/environment",  EnvironmentCurrentController.INSTANCE.getCurrent());
      applicationEventPublisher.publishEvent(CurrentJobShouldStopEvent.builder().shouldStopJob(Boolean.TRUE).build());
    }
  }
  
  @EventListener
  public void amountSendingListener(AmountSendingEvent event) {
    simpMessagingTemplate.convertAndSend("/topic/sending/amount", event);
  }
  
}
