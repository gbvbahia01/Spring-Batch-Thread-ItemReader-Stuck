package br.com.gbvbahia.threads.monitor.service;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.persistence.repository.ProcessorRepository;
import br.com.gbvbahia.threads.monitor.util.TestDTOFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
class ProcessorServiceTest {

  @Autowired
  private ProcessorService processorService;
  @Autowired
  private ProcessorRepository processorRepository;

  @BeforeEach
  void setUp() throws Exception {
    processorRepository.deleteAll();
  }

  @Test
  void testStartNewProcess() {
    ProcessorDTO dto = TestDTOFactory.processorDTO();

    ProcessorDTO saved = processorService.startNewProcess(dto);

    Assertions.assertNotNull(saved.getId());
    Assertions.assertEquals(dto.getDataToProcess(), saved.getDataToProcess());
    Assertions.assertEquals(dto.getName(), saved.getName());
    Assertions.assertEquals(ProcessStatus.WAITING, saved.getProcessStatus());
    Assertions.assertEquals(dto.getUrlToCall(), saved.getUrlToCall());

    Assertions.assertTrue(processorRepository.findById(saved.getId()).isPresent());

  }

  @Test
  void testFindNextToBeProcessed() {
    ProcessorDTO dto = TestDTOFactory.processorDTO();

    processorService.startNewProcess(dto);
    
    Optional<Processor> optToProcess = processorService.findNextToBeProcessed();
    
    Assertions.assertTrue(optToProcess.isPresent());
    
    Processor saved = optToProcess.get();
    
    Assertions.assertNotNull(saved.getId());
    Assertions.assertEquals(dto.getDataToProcess(), saved.getDataToProcess());
    Assertions.assertEquals(dto.getName(), saved.getName());
    Assertions.assertEquals(ProcessStatus.PROCESSING, saved.getProcessStatus());
    Assertions.assertEquals(dto.getUrlToCall(), saved.getUrlToCall());
    
    
  }

}
