package br.com.gbvbahia.fake.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.fake.event.AmountSendingEvent;
import br.com.gbvbahia.fake.event.EnvironmentChangedEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FakeEventsListener {

  private final SimpMessagingTemplate simpMessagingTemplate;
  
  @EventListener
  public void environmentChangedListener(EnvironmentChangedEvent event) {
    if (event.isEnvironmentChanged()) {
      simpMessagingTemplate.convertAndSend("/topic/environment",  EnvironmentCurrentController.INSTANCE.getCurrent());
    }
  }
  
  @EventListener
  public void amountSendingListener(AmountSendingEvent event) {
    simpMessagingTemplate.convertAndSend("/topic/sending/amount", event);
  }
  
}
