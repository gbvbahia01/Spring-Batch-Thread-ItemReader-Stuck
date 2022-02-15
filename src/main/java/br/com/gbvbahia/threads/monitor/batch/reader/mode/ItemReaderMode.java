package br.com.gbvbahia.threads.monitor.batch.reader.mode;

import java.util.Optional;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;


public interface ItemReaderMode {

  boolean workWithMode(BatchItemReaderMode readerMode);

  Optional<Processor> read(Optional<Processor> itemReader);

}
