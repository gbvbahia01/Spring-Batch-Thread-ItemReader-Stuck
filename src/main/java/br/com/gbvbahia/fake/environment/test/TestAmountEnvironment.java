package br.com.gbvbahia.fake.environment.test;

import br.com.gbvbahia.fake.environment.AbsAmountEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;

@Builder
public class TestAmountEnvironment extends AbsAmountEnvironmentContract {

  @Override
  protected Environment getEnviroment() {
    return Environment.TEST;
  }

  @Override
  protected int factor() {
    return 2;
  }

}
