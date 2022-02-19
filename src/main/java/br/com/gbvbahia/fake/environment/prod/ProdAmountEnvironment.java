package br.com.gbvbahia.fake.environment.prod;

import br.com.gbvbahia.fake.environment.AbsAmountEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;

@Builder
public class ProdAmountEnvironment extends AbsAmountEnvironmentContract {

  @Override
  protected Environment getEnviroment() {
    return Environment.PROD;
  }

  @Override
  protected int factor() {
    return 6;
  }


}
