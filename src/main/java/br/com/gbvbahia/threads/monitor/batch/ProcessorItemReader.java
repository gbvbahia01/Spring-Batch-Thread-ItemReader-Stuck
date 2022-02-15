package br.com.gbvbahia.threads.monitor.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderCounterToNullMode;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderMode;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderNeverNullMode;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderNullMode;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@RequiredArgsConstructor
public class ProcessorItemReader implements ItemReader<Optional<Processor>> {

  private final BatchItemReaderMode batchItemReaderMode;
  private final ProcessorService processorService;
  private final Integer amountThreads;
  private final Integer threadFactor;
  private final List<ItemReaderMode> itemsMode = new ArrayList<>(); 
  
  @Override
  public Optional<Processor> read() throws Exception {
    
    Optional<Processor> toProcess = processorService.findNextToBeProcessed();
    
    for (ItemReaderMode itemMode : itemsMode) {
      if (itemMode.workWithMode(batchItemReaderMode)) {
        return itemMode.read(toProcess);
      }
    }
    
    String error = String.format("There is not a ItemReaderMode that works with: %s", batchItemReaderMode);
    log.error(error);
    throw new IllegalArgumentException(error);
  }
  
  
  @BeforeStep
  public void retrieveInterstepData(StepExecution stepExecution) {
    itemsMode.clear();
    itemsMode.add(new ItemReaderNullMode());
    itemsMode.add(new ItemReaderNeverNullMode());
    itemsMode.add(new ItemReaderCounterToNullMode(amountThreads * threadFactor));
  }

}
