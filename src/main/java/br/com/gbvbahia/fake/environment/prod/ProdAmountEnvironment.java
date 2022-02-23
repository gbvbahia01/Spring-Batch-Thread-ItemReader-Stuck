package br.com.gbvbahia.fake.environment.prod;

import java.time.LocalDateTime;
import br.com.gbvbahia.fake.environment.AbsAmountEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class ProdAmountEnvironment extends AbsAmountEnvironmentContract {

  @Override
  protected Environment getEnviroment() {
    return Environment.PROD;
  }

  @Override
  public Integer amount() {
    int amount = (LocalDateTime.now().getSecond() / 5 + 1) * 5;
    log.trace("Amount: {} Second: {}", amount, LocalDateTime.now().getSecond());
    return amount;
  }


}
