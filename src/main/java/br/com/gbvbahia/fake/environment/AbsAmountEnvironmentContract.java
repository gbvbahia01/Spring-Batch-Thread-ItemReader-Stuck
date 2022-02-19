package br.com.gbvbahia.fake.environment;

import java.time.LocalDateTime;

public abstract class AbsAmountEnvironmentContract implements AmountEnvironmentContract {

  @Override
  public boolean canHandle(Environment environment) {
    return getEnviroment().equals(environment);
  }

  @Override
  public Integer amount() {
    
    final int amount = (LocalDateTime.now().getSecond() / 5 + 1) * factor();
    return amount;
  }

  
  protected abstract int factor();
  protected abstract Environment getEnviroment();
}
