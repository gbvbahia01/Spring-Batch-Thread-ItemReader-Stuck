package br.com.gbvbahia.threads.monitor.batch.reader.mode;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;

public class ItemReaderCounterToNullMode implements ItemReaderMode {

  private static final AtomicInteger COUNTER = new AtomicInteger();
  private final Integer LIMIT_TO_RETURN;
  
  public ItemReaderCounterToNullMode(Integer lIMIT_TO_RETURN) {
    super();
    LIMIT_TO_RETURN = lIMIT_TO_RETURN;
    COUNTER.set(0);
  }

  @Override
  public boolean workWithMode(BatchItemReaderMode readerMode) {
    return BatchItemReaderMode.COUNTER_TO_NULL.equals(readerMode);
  }
  
  @Override
  public Optional<Processor> read(Optional<Processor> itemReader) {
    if (COUNTER.getAndIncrement() > LIMIT_TO_RETURN) {
      return null;
    }
    return itemReader;
  }
}
