package br.com.gbvbahia.threads.monitor.rest;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import br.com.gbvbahia.threads.monitor.until.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constants.Rest.PATH_V1)
@RequiredArgsConstructor
@Slf4j
public class ProcessorController {

  private final ProcessorService processorService;

  @PostMapping(value = Constants.Rest.PROCESSOR, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProcessorDTO> add(@Valid @RequestBody ProcessorDTO processor) {

    log.trace("New Processor requested:{}", processor);

    try {
      
      return ResponseEntity.status(HttpStatus.CREATED)
             .body(processorService.startNewProcess(processor));
      
    } catch (Exception e) {
      
      log.error("Error saving processor.", e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
