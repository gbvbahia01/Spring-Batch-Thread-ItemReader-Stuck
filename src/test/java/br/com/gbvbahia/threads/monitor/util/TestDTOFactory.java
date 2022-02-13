package br.com.gbvbahia.threads.monitor.util;

import com.github.javafaker.Faker;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;

public class TestDTOFactory {

  private static Faker faker = Faker.instance();
  
  public static ProcessorDTO processorDTO() {
    return ProcessorDTO.builder()
        .dataToProcess(faker.crypto().sha512())
        .name(faker.company().name())
        .urlToCall(faker.internet().url())
        .build();
  }
  
}
