package br.com.gbvbahia.threads.monitor.batch.reader.mode;

import java.util.Optional;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;

public class ItemReaderNeverNullMode implements ItemReaderMode {

  @Override
  public boolean workWithMode(BatchItemReaderMode readerMode) {
    return BatchItemReaderMode.NEVER_NULL.equals(readerMode);
  }
  
  @Override
  public Optional<Processor> read(Optional<Processor> itemReader) {
    return itemReader;
  }
}
