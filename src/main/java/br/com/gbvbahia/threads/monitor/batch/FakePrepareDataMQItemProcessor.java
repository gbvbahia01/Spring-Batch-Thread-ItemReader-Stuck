package br.com.gbvbahia.threads.monitor.batch;

import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;

import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.model.Processor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakePrepareDataMQItemProcessor implements ItemProcessor<Optional<Processor>, Optional<Processor>> {

	@Override
	public Optional<Processor> process(Optional<Processor> item) throws Exception {

		if (item.isPresent()) {
			Processor processor = item.get();
			if (processor.getDataToProcess().length() % 2 == 0) {
				processor.setDataResult("PAIR");
			} else {
				processor.setDataResult("ODD");
			}
			processor.setProcessStatus(ProcessStatus.FINISHED);
			processor.setProcessResult("Processed");
		}

		return item;
	}

}
