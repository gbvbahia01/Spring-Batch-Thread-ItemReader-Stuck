package br.com.gbvbahia.fake.environment;

public interface AmountEnvironmentContract {

  boolean canHandle(Environment environment);
  
  Integer amount();
}
