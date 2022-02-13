package br.com.gbvbahia.threads.monitor.persistence.repository;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.model.Processor;

@Repository
public interface ProcessorRepository extends JpaRepository<Processor, Long> {
  
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Processor> findByProcessStatus(ProcessStatus processStatus);
}
