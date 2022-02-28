package br.com.gbvbahia.threads.monitor.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import br.com.gbvbahia.fake.environment.Environment;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderCounterToNullMode;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderMode;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderNeverNullMode;
import br.com.gbvbahia.threads.monitor.batch.reader.mode.ItemReaderNullMode;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.dto.BatchModeController;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class ProcessorItemReader implements ItemReader<Optional<Processor>> {

  private final BatchItemReaderMode batchItemReaderMode;
  private final Environment environment;
  private final ProcessorService processorService;
  private final List<ItemReaderMode> itemsMode = new ArrayList<>();
  
  @Override
  public Optional<Processor> read() throws Exception {
    
    if (shouldFinishCurrentJob()) {
      log.info("This job must be finished.");
      return null;
    }
    
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
  
  
  private boolean shouldFinishCurrentJob() {
   
    return !EnvironmentCurrentController.INSTANCE.getCurrent().equals(environment)
        || !BatchModeController.INSTANCE.getBatchMode().equals(batchItemReaderMode);
  }


  @BeforeStep
  public void retrieveInterStepData(StepExecution stepExecution) {
    itemsMode.clear();
    itemsMode.add(new ItemReaderNullMode());
    itemsMode.add(new ItemReaderNeverNullMode());
    itemsMode.add(new ItemReaderCounterToNullMode(processorService.countByStatusWaiting()));
  }
}
