package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;

import br.com.gbvbahia.threads.monitor.model.Processor;
import br.com.gbvbahia.threads.monitor.service.ProcessorMQSenderService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class FakeSendDataToMQItemProcessor implements ItemProcessor<Optional<Processor>, Optional<Processor>> {

	private final ProcessorMQSenderService processorMQSenderService;
	
	@Override
	public Optional<Processor> process(Optional<Processor> item) throws Exception {

		if (item.isPresent()) {
			Processor processor = item.get();
			processorMQSenderService.sendDataToMQ(processor.getDataResult());
		}

		return item;
	}

}
