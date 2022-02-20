package br.com.gbvbahia.threads.monitor.listener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import br.com.gbvbahia.threads.monitor.dto.TaskExecutionDTO;
import br.com.gbvbahia.threads.monitor.event.BatchTaskExecutorInfoEvent;

class BatchEventsListenerTest {

  private BatchEventsListener listener;
  
  @Mock
  private SimpMessagingTemplate simpMessagingTemplate;
  
  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    listener = new BatchEventsListener(simpMessagingTemplate);
    ReflectionTestUtils.setField(listener, "amountThreads", 10);
  }

  @Test
  void testBatchTaskExecutorInfoListener_40_percent() {
    
    BatchTaskExecutorInfoEvent event = BatchTaskExecutorInfoEvent.builder()
    .activeCount(4)
    .corePoolSize(10)
    .maxPoolSize(11)
    .poolSize(12)
    .build();
    
    ArgumentCaptor<TaskExecutionDTO> dtoCaptor = ArgumentCaptor.forClass(TaskExecutionDTO.class);
    
    listener.batchTaskExecutorInfoListener(event);
    
    Mockito.verify(simpMessagingTemplate, times(1)).convertAndSend(Mockito.eq("/topic/taskExecutorInfo"), dtoCaptor.capture());
    
    TaskExecutionDTO dto = dtoCaptor.getValue();

    assertNotNull(dto);
    assertEquals(10, dto.getCorePoolSize());
    assertEquals(11, dto.getMaxPoolSize());
    assertEquals(12, dto.getPoolSize());
    assertEquals(4, dto.getActiveCount());
    assertEquals(10, dto.getYmlAmountThreads());
    assertEquals(40, dto.getPercent());
    assertEquals("bg-danger", dto.getColor());
  }

  @Test
  void testBatchTaskExecutorInfoListener_100_percent() {
    
    BatchTaskExecutorInfoEvent event = BatchTaskExecutorInfoEvent.builder()
    .activeCount(10)
    .corePoolSize(10)
    .maxPoolSize(11)
    .poolSize(12)
    .build();
    
    ArgumentCaptor<TaskExecutionDTO> dtoCaptor = ArgumentCaptor.forClass(TaskExecutionDTO.class);
    
    listener.batchTaskExecutorInfoListener(event);
    
    Mockito.verify(simpMessagingTemplate, times(1)).convertAndSend(Mockito.eq("/topic/taskExecutorInfo"), dtoCaptor.capture());
    
    TaskExecutionDTO dto = dtoCaptor.getValue();

    assertNotNull(dto);
    assertEquals(10, dto.getCorePoolSize());
    assertEquals(11, dto.getMaxPoolSize());
    assertEquals(12, dto.getPoolSize());
    assertEquals(10, dto.getActiveCount());
    assertEquals(10, dto.getYmlAmountThreads());
    assertEquals(100, dto.getPercent());
    assertEquals("bg-success", dto.getColor());
  }
  
  @Test
  void testBatchTaskExecutorInfoListener_80_percent() {
    
    BatchTaskExecutorInfoEvent event = BatchTaskExecutorInfoEvent.builder()
    .activeCount(8)
    .corePoolSize(10)
    .maxPoolSize(11)
    .poolSize(12)
    .build();
    
    ArgumentCaptor<TaskExecutionDTO> dtoCaptor = ArgumentCaptor.forClass(TaskExecutionDTO.class);
    
    listener.batchTaskExecutorInfoListener(event);
    
    Mockito.verify(simpMessagingTemplate, times(1)).convertAndSend(Mockito.eq("/topic/taskExecutorInfo"), dtoCaptor.capture());
    
    TaskExecutionDTO dto = dtoCaptor.getValue();

    assertNotNull(dto);
    assertEquals(10, dto.getCorePoolSize());
    assertEquals(11, dto.getMaxPoolSize());
    assertEquals(12, dto.getPoolSize());
    assertEquals(8, dto.getActiveCount());
    assertEquals(10, dto.getYmlAmountThreads());
    assertEquals(80, dto.getPercent());
    assertEquals("bg-warning", dto.getColor());
  }
}
