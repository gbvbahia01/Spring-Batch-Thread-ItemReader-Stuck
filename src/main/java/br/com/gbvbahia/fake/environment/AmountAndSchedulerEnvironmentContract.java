package br.com.gbvbahia.fake.environment;

public interface AmountAndSchedulerEnvironmentContract {

  boolean canHandle(Environment environment);
  
  Integer amountToRequest();
  Long nextExecutionMilliseconds();
}
