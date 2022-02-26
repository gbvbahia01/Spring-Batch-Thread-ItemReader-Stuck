package br.com.gbvbahia.threads.monitor.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.dto.BatchModeController;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import br.com.gbvbahia.threads.monitor.event.BatchReadModeChangedEvent;
import br.com.gbvbahia.threads.monitor.event.MaxDifferenceBetweenCreatedAndFinishedEvent;
import br.com.gbvbahia.threads.monitor.event.ProcessorCounterEvent;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.persistence.repository.ProcessorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessorService {

  @Value("${app.batch.idle}")
  private Integer IDLE_PROCESS;

  @Value("${app.batch.old}")
  private Integer OLD_PROCESS;

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ModelMapper modelMapper;
  private final ProcessorRepository processorRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Optional<Processor> findNextToBeProcessed() {

    Optional<Processor> nextOpt =
        processorRepository.findFirstByProcessStatusOrderById(ProcessStatus.WAITING);

    if (nextOpt.isEmpty()) {
      return nextOpt;
    }

    Processor toProcess = nextOpt.get();

    toProcess.setProcessStatus(ProcessStatus.PROCESSING);
    toProcess.setStartProcess(LocalDateTime.now());
    processorRepository.save(toProcess);

    return Optional.of(toProcess);

  }
  
  @Transactional(propagation = Propagation.REQUIRED)
  public ProcessorDTO startNewProcess(ProcessorDTO dto) {

    Processor processor =
        Processor.builder().dataToProcess(dto.getDataToProcess()).name(dto.getName())
            .urlToCall(dto.getUrlToCall()).processStatus(ProcessStatus.WAITING).build();

    Processor saved = processorRepository.save(processor);
    log.trace("Processor saved:{}", saved);

    return modelMapper.map(saved, ProcessorDTO.class);

  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveProcessResult(Processor processor) {
    processor.setEndProcess(LocalDateTime.now());
    processorRepository.save(processor);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void releaseIdleProcess() {
    LocalDateTime idleTime = LocalDateTime.now().minus(IDLE_PROCESS, ChronoUnit.MILLIS);
    processorRepository.findByProcessStatusAndUpdatedAtBefore(ProcessStatus.PROCESSING, idleTime)
        .forEach(process -> {
          process.setProcessStatus(ProcessStatus.WAITING);
          processorRepository.save(process);
        });
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteOldProcess() {
    LocalDateTime idleTime = LocalDateTime.now().minus(OLD_PROCESS, ChronoUnit.MILLIS);
    processorRepository.deleteByProcessStatusAndUpdatedAtBefore(ProcessStatus.FINISHED, idleTime);
  }

  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public void generateProcessorCounterEvent() {
    
    Integer waiting = processorRepository.countByProcessStatus(ProcessStatus.WAITING);
    Integer processing = processorRepository.countByProcessStatus(ProcessStatus.PROCESSING);
    Integer finished = processorRepository.countByProcessStatus(ProcessStatus.FINISHED);
    
    ProcessorCounterEvent event = ProcessorCounterEvent.builder().waiting(waiting)
    .processing(processing)
    .finished(finished)
    .build();
    
    applicationEventPublisher.publishEvent(event);
    
  }
  
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public Integer countByStatusWaiting() {
    return processorRepository.countByProcessStatus(ProcessStatus.WAITING).intValue();
  }
  
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public void maxDiferenceBetweenCreatedAndFinishedEvent() {
    Optional<Double> maxDifference = processorRepository.maxDiferenceBetweenCreatedAndFinished();
    
    applicationEventPublisher.publishEvent(
        MaxDifferenceBetweenCreatedAndFinishedEvent.builder().maxDifference(maxDifference.orElse(0.0).longValue())
        .build());
  }

  public void changeReadMode(BatchItemReaderMode readModeTo) {
    boolean readModeDoesChange = !BatchModeController.INSTANCE.getBatchMode().equals(readModeTo);

    log.info("BatchItemReaderMode change from: {} to {}",
        BatchModeController.INSTANCE.getBatchMode(), readModeTo);

    BatchModeController.INSTANCE.setBatchMode(readModeTo);

    applicationEventPublisher.publishEvent(
        BatchReadModeChangedEvent.builder().readModeChanged(readModeDoesChange)
        .build());
  }

}
