package br.com.gbvbahia.fake.environment.qa;

import br.com.gbvbahia.fake.environment.AbsAmountAndSchedulerEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;

@Builder
public class QAAmountEnvironment extends AbsAmountAndSchedulerEnvironmentContract {

	private final static Long NEXT_EXECUTION_MILLISECONDS = 5000L;
	private final static Integer AMOUNT_TO_REQUEST = 16;

	@Override
	protected Environment getEnviroment() {
		return Environment.QA;
	}

	@Override
	public Integer amountToRequest() {
		return AMOUNT_TO_REQUEST;
	}

	@Override
	public Long nextExecutionMilliseconds() {
		return NEXT_EXECUTION_MILLISECONDS;
	}


}
