package br.com.gbvbahia.threads.monitor.batch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemWriter;
import org.springframework.util.CollectionUtils;
import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class ProcessorItemWriter implements ItemWriter<Optional<Processor>> {

  private final ProcessorService processorService;

  @Override
  public void write(List<? extends Optional<Processor>> items) throws Exception {

    if (!CollectionUtils.isEmpty(items)) {

      items.stream().filter(opt -> opt != null && opt.isPresent()).collect(Collectors.toList())
          .forEach(opt -> processorService.save(opt.get()));
    }

  }

}
