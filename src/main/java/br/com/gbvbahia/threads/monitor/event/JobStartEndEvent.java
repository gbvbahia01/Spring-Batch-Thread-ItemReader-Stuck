package br.com.gbvbahia.threads.monitor.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class JobStartEndEvent {

  private final boolean startJob;
  private final Long jobId;
  private final String jobName;
}
