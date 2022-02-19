package br.com.gbvbahia.fake.environment;

public enum EnvironmentCurrentController {

  INSTANCE(Environment.TEST);
  
  private Environment current;

  private EnvironmentCurrentController(Environment current) {
    this.current = current;
  }

  public Environment getCurrent() {
    return current;
  }

  public void setCurrent(Environment current) {
    this.current = current;
  }
  
  
}
