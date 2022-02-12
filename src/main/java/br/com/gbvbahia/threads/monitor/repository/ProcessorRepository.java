package br.com.gbvbahia.threads.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.gbvbahia.threads.monitor.model.Processor;

@Repository
public interface ProcessorRepository extends JpaRepository<Processor, Long> {}
