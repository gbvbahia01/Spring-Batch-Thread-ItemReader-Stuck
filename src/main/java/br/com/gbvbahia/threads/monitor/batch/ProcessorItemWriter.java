package br.com.gbvbahia.threads.monitor.batch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import br.com.gbvbahia.threads.monitor.event.BatchTaskExecutorInfoEvent;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Builder
@Slf4j
public class ProcessorItemWriter implements ItemWriter<Optional<Processor>> {

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ProcessorService processorService;
  private final ThreadPoolTaskExecutor taskExecutor;

  @Override
  public void write(List<? extends Optional<Processor>> items) throws Exception {

    if (!CollectionUtils.isEmpty(items)) {

      items.stream().filter(opt -> opt != null && opt.isPresent()).collect(Collectors.toList())
          .forEach(opt -> processorService.save(opt.get()));
    }

    BatchTaskExecutorInfoEvent event = BatchTaskExecutorInfoEvent.builder()
        .activeCount(taskExecutor.getActiveCount())
        .corePoolSize(taskExecutor.getCorePoolSize())
        .maxPoolSize(taskExecutor.getMaxPoolSize())
        .poolSize(taskExecutor.getPoolSize()).build();

    log.trace("Task info event: {}", event);

    applicationEventPublisher.publishEvent(event);
  }

}
