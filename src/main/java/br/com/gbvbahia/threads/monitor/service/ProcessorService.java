package br.com.gbvbahia.threads.monitor.service;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.persistence.repository.ProcessorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessorService {

  private final ModelMapper modelMapper;
  private final ProcessorRepository processorRepository;

  @Transactional(propagation = Propagation.REQUIRED)
  public ProcessorDTO startNewProcess(ProcessorDTO dto) {

    Processor processor =
        Processor.builder().dataToProcess(dto.getDataToProcess()).name(dto.getName())
            .urlToCall(dto.getUrlToCall()).processStatus(ProcessStatus.WAITING).build();

    Processor saved = processorRepository.save(processor);
    log.debug("Processor saved:{}", saved);

    return modelMapper.map(saved, ProcessorDTO.class);

  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Optional<Processor> findNextToBeProcessed() {

    Optional<Processor> nextOpt = processorRepository.findFirstByProcessStatusOrderById(ProcessStatus.WAITING);

    if (nextOpt.isEmpty()) {
      return nextOpt;
    }

    Processor toProcess = nextOpt.get();

    toProcess.setProcessStatus(ProcessStatus.PROCESSING);
    processorRepository.save(toProcess);

    return Optional.of(toProcess);

  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void save(Processor processor) {
    processorRepository.save(processor);
  }

}
