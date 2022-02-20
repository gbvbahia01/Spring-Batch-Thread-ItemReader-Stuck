package br.com.gbvbahia.threads.monitor.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class ProcessorCounterEvent {

  private final Integer waiting;
  private final Integer processing;
  private final Integer finished;
  
}
