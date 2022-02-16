package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;
import org.springframework.batch.item.ItemProcessor;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorApiCallerService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class FakeItemProcessor implements ItemProcessor<Optional<Processor>, Optional<Processor>> {

  private final ProcessorApiCallerService processorApiCallerService;
  
  @Override
  public Optional<Processor> process(Optional<Processor> item) throws Exception {
    
    if (item.isPresent()) {
      Processor processor = item.get();
      String processResult = processorApiCallerService.requestProcessResult(processor.getUrlToCall());
      processor.setProcessResult(processResult);
      processor.setProcessStatus(ProcessStatus.FINISHED);
    }
    
    return item;
  }

}
