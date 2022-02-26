package br.com.gbvbahia.threads.monitor.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.model.Processor;

@Repository
public interface ProcessorRepository extends JpaRepository<Processor, Long> {
  
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Processor> findFirstByProcessStatusOrderById(ProcessStatus processStatus);
  
  void deleteByProcessStatusAndUpdatedAtBefore(ProcessStatus processStatus, LocalDateTime updatedAt);
  
  List<Processor> findByProcessStatusAndUpdatedAtBefore(ProcessStatus processStatus, LocalDateTime updatedAt);
  
  Integer countByProcessStatus(ProcessStatus processStatus);
  
  @Query(nativeQuery = true,
         value = "SELECT EXTRACT(EPOCH FROM (end_at - created_at)) AS difference FROM PROCESSOR where PROCESS_STATUS = 'FINISHED' order by id desc limit 1")
  Optional<Double> maxDiferenceBetweenCreatedAndFinished();
}
