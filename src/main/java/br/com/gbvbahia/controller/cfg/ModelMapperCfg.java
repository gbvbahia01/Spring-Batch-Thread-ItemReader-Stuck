package br.com.gbvbahia.controller.cfg;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ModelMapperCfg {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mm = new ModelMapper();
    mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    log.info("ModelMapperCfg bean created.");
    return mm;
  }
}
