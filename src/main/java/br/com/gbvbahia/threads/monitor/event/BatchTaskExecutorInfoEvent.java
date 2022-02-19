package br.com.gbvbahia.threads.monitor.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class BatchTaskExecutorInfoEvent {

  private final int corePoolSize;
  private final int maxPoolSize;
  private final int poolSize;
  private final int activeCount;
  
}
