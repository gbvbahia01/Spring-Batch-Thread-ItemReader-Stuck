package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.springframework.batch.item.ItemProcessor;
import com.github.javafaker.Faker;
import br.com.gbvbahia.threads.monitor.model.Processor;

public class FakeItemProcessor implements ItemProcessor<Optional<Processor>, Processor> {

  private Faker faker = Faker.instance();

  @Override
  public Processor process(Optional<Processor> item) throws Exception {
    
    LockSupport.parkUntil(TimeUnit.SECONDS.toMillis(faker.number().numberBetween(150, 500)));
    
    return item.get();
  }

}
