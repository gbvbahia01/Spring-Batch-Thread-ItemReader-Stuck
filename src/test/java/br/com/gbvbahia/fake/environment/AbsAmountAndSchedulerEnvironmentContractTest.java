package br.com.gbvbahia.fake.environment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AbsAmountAndSchedulerEnvironmentContractTest {

  @BeforeEach
  void setUp() throws Exception {}

  void test() throws Exception {

    execute("TEST", 2);
    execute("QA", 1);
    execute("PROD", 6);
  }

  private void execute(String enviroment, Integer factor) throws Exception {
    
    log.info("Starting: {}", enviroment);
    Instant limit = Instant.now().plus(1, ChronoUnit.MINUTES);

    while (limit.isAfter(Instant.now())) {

      final int amount = (LocalDateTime.now().getSecond() / 5 + 1) * factor;
      log.info("{}-Amount: {}", enviroment, amount);

      Thread.sleep(1000);
    }
    log.info("Finished: {}", enviroment);
  }
  
}
