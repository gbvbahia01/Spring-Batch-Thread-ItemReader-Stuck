package br.com.gbvbahia.fake.environment.prod;

import java.time.LocalDateTime;

import com.github.javafaker.Faker;

import br.com.gbvbahia.fake.environment.AbsAmountAndSchedulerEnvironmentContract;
import br.com.gbvbahia.fake.environment.Environment;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class ProdAmountEnvironment extends AbsAmountAndSchedulerEnvironmentContract {

	private final Faker faker = Faker.instance();
	
	@Override
	protected Environment getEnviroment() {
		return Environment.PROD;
	}

	@Override
	public Integer amountToRequest() {
		int amount = faker.number().numberBetween(2, 3);
	    log.trace("Amount: {} Second: {}", amount, LocalDateTime.now().getSecond());
	    return amount;
	}

	@Override
	public Long nextExecutionMilliseconds() {
		return faker.number().numberBetween(500L, 1000L);
	}

}
