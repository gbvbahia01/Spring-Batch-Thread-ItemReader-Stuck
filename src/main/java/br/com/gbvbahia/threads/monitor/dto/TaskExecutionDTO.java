package br.com.gbvbahia.threads.monitor.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class TaskExecutionDTO {

  private final int corePoolSize;
  private final int maxPoolSize;
  private final int poolSize;
  private final int activeCount;
  
  private final int percent;
  private final int ymlAmountThreads;
  private final String color;
}
