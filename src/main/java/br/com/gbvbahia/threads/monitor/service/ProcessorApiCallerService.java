package br.com.gbvbahia.threads.monitor.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.springframework.stereotype.Service;
import com.github.javafaker.Faker;

@Service
public class ProcessorApiCallerService {

  private Faker faker = Faker.instance();

  public String requestProcessResult(String url) {
    
    LockSupport.parkUntil(TimeUnit.SECONDS.toMillis(faker.number().numberBetween(150, 500)));
    
    return faker.job().title();
    
  }

}
