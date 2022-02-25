package br.com.gbvbahia.fake.environment;

public abstract class AbsAmountAndSchedulerEnvironmentContract implements AmountAndSchedulerEnvironmentContract {

  @Override
  public boolean canHandle(Environment environment) {
    return getEnviroment().equals(environment);
  }

  protected abstract Environment getEnviroment();
}
