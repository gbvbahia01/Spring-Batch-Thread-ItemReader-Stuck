package br.com.gbvbahia.fake.environment.test;

import br.com.gbvbahia.fake.environment.AbsAmountAndSchedulerEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;

@Builder
public class TestAmountEnvironment extends AbsAmountAndSchedulerEnvironmentContract {

	private final static Long NEXT_EXECUTION_MILLISECONDS = 30000L;
	private final static Integer AMOUNT_TO_REQUEST = 180;

	@Override
	protected Environment getEnviroment() {
		return Environment.TEST;
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
