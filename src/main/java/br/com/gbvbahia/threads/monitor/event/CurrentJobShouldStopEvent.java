package br.com.gbvbahia.threads.monitor.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class CurrentJobShouldStopEvent {

  private final boolean shouldStopJob;
  
}
