package br.com.gbvbahia.fake.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import br.com.gbvbahia.fake.environment.Environment;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.fake.event.EnvironmentChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FakeService {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void changeEnvironment(Environment environment) {

    boolean environmentDoesChange =
        !EnvironmentCurrentController.INSTANCE.getCurrent().equals(environment);

    log.info("Environment change from: {} to {}",
        EnvironmentCurrentController.INSTANCE.getCurrent(), environment);

    EnvironmentCurrentController.INSTANCE.setCurrent(environment);

    applicationEventPublisher.publishEvent(
        EnvironmentChangedEvent.builder().environmentChanged(environmentDoesChange)
        .build());
  }
}
