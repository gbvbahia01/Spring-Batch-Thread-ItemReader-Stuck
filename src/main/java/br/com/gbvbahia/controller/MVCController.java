package br.com.gbvbahia.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.gbvbahia.fake.environment.Environment;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.fake.service.FakeService;
import br.com.gbvbahia.threads.monitor.dto.BatchItemReaderMode;
import br.com.gbvbahia.threads.monitor.dto.BatchModeController;
import br.com.gbvbahia.threads.monitor.service.ProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping({"/", Mapping.PATH_BASE, Mapping.Fake.PATH})
@Slf4j
@RequiredArgsConstructor
public class MVCController {

  private final FakeService fakeService;
  private final ProcessorService processorService;
  
  @PostConstruct
  void init() {
    log.info("FakeController started.");
  }

  @GetMapping
  public String batchsPage(final Model model, HttpServletRequest request,
      HttpServletResponse response) {
    log.trace("start page");

    model.addAttribute("environment", EnvironmentCurrentController.INSTANCE.getCurrent());
    model.addAttribute("environments", new Environment[] {Environment.TEST, Environment.PROD});
    
    model.addAttribute("readmode", BatchModeController.INSTANCE.getBatchMode());
    model.addAttribute("readmodes", BatchItemReaderMode.values());

    return Pages.MONITORING.pageName;
  }

  @PutMapping(value = Mapping.Fake.CHANGE_ENVIRONMENT)
  public ResponseEntity<Void> changeEnvironment(@PathVariable("env") @NotNull Integer envOrdinal) {

    try {
      
      Environment environmentTo = Environment.values()[envOrdinal];
      fakeService.changeEnvironment(environmentTo);
      return ResponseEntity.ok().build();
      
    } catch (Exception e) {
      
      String error = String.format("Error on process envOrdinal: %d. %s", envOrdinal, e.getMessage());
      log.error(error, e);
      return ResponseEntity.internalServerError().build();
    }
  }
  
  @PutMapping(value = Mapping.Fake.CHANGE_READ_MODE)
  public ResponseEntity<Void> changeReadMode(@PathVariable("mode") @NotNull Integer modeOrdinal) {

    try {
      
      BatchItemReaderMode readModeTo = BatchItemReaderMode.values()[modeOrdinal];
      processorService.changeReadMode(readModeTo);
      return ResponseEntity.ok().build();
      
    } catch (Exception e) {
      
      String error = String.format("Error on process envOrdinal: %d. %s", modeOrdinal, e.getMessage());
      log.error(error, e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
