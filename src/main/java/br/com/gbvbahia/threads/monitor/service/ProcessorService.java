package br.com.gbvbahia.threads.monitor.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.repository.ProcessorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessorService {

  private final ModelMapper modelMapper;
  private final ProcessorRepository processorRepository;

  public ProcessorDTO save(ProcessorDTO dto) {
    
    Processor processor = Processor.builder()
        .bearerToken(dto.getBearerToken())
        .name(dto.getName())
        .urlToCall(dto.getUrlToCall())
        .build();
    
    Processor saved = processorRepository.save(processor);
    log.debug("Processor saved:{}", saved);
    
    return modelMapper.map(saved, ProcessorDTO.class);

  }

}
