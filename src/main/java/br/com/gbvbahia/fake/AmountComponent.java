package br.com.gbvbahia.fake;

import java.util.List;
import org.springframework.stereotype.Component;
import br.com.gbvbahia.fake.environment.AmountEnvironmentContract;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.fake.environment.prod.ProdAmountEnvironment;
import br.com.gbvbahia.fake.environment.qa.QAAmountEnvironment;
import br.com.gbvbahia.fake.environment.test.TestAmountEnvironment;

@Component
public class AmountComponent {

  private final List<AmountEnvironmentContract> amountEnvironmentContracts =
      List.of(TestAmountEnvironment.builder().build(),
              QAAmountEnvironment.builder().build(),
              ProdAmountEnvironment.builder().build());

  public Integer amountToSend() {
    
    return amountEnvironmentContracts.stream()
    .filter(contract -> contract.canHandle(EnvironmentCurrentController.INSTANCE.getCurrent()))
    .findFirst().get().amount();
    
  }

}
