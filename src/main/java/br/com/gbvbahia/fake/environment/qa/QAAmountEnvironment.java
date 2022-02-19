package br.com.gbvbahia.fake.environment.qa;

import br.com.gbvbahia.fake.environment.AbsAmountEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;

@Builder
public class QAAmountEnvironment extends AbsAmountEnvironmentContract {

  @Override
  protected Environment getEnviroment() {
    return Environment.QA;
  }

  @Override
  protected int factor() {
    return 1;
  }


}
