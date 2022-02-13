package br.com.gbvbahia.threads.monitor.persistence.h2;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class H2CleanerScheduler {

  private final H2CleanerRepository h2CleanerRepository;
  
  @Scheduled(fixedRateString = "${app.h2.scheduler.clean}")
  public void callH2Cleaner() {
    log.trace("Init Job memory cleaner");
    h2CleanerRepository.cleanMemoryCompletedJobs();
    log.trace("End Job memory cleaner");
  }
}
