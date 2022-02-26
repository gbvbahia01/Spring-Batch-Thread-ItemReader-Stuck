package br.com.gbvbahia.threads.monitor.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class MaxDifferenceBetweenCreatedAndFinishedEvent {

  private final Long maxDifference;
}
