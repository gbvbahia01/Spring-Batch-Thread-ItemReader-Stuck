package br.com.gbvbahia.fake.environment;

public abstract class AbsAmountEnvironmentContract implements AmountEnvironmentContract {

  @Override
  public boolean canHandle(Environment environment) {
    return getEnviroment().equals(environment);
  }

  public abstract Integer amount();
    
  protected abstract Environment getEnviroment();
}
