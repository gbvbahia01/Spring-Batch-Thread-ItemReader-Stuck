package br.com.gbvbahia.threads.monitor.batch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemWriter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Builder
@Slf4j
public class ProcessorItemWriter implements ItemWriter<Optional<Processor>> {

  private final ProcessorService processorService;
  private final ThreadPoolTaskExecutor taskExecutor;
  
  @Override
  public void write(List<? extends Optional<Processor>> items) throws Exception {

    if (!CollectionUtils.isEmpty(items)) {

      items.stream().filter(opt -> opt != null && opt.isPresent()).collect(Collectors.toList())
          .forEach(opt -> processorService.save(opt.get()));
    }
    
    String pool = String.format("Properties corePoolSize:%d maxPoolSize:%d, poolSize:%d, activeCount:%d",
        taskExecutor.getCorePoolSize(),
        taskExecutor.getMaxPoolSize(),
        taskExecutor.getPoolSize(),
        taskExecutor.getActiveCount());
    log.trace(pool);
  }

}
