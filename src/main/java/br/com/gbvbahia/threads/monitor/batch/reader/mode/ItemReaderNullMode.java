package br.com.gbvbahia.threads.monitor.batch.reader.mode;

import java.util.Optional;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;

public class ItemReaderNullMode implements ItemReaderMode {

  @Override
  public boolean workWithMode(BatchItemReaderMode readerMode) {
    return BatchItemReaderMode.RETURN_NULL.equals(readerMode);
  }
  
  @Override
  public Optional<Processor> read(Optional<Processor> itemReader) {
    if(itemReader.isEmpty()) {
      return null;
    }
    return itemReader;
  }
}
