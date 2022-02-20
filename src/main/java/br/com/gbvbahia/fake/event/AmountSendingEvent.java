package br.com.gbvbahia.fake.event;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class AmountSendingEvent {

  private final Integer amoundSending;
}
