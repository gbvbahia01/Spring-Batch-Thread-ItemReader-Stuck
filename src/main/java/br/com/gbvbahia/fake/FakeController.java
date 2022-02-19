package br.com.gbvbahia.fake;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.gbvbahia.fake.environment.Environment;
import br.com.gbvbahia.fake.environment.EnvironmentCurrentController;
import br.com.gbvbahia.threads.monitor.controller.Mapping;
import br.com.gbvbahia.threads.monitor.controller.Pages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping({"/", Mapping.PATH_BASE, Mapping.Fake.PATH})
@Slf4j
@RequiredArgsConstructor
public class FakeController {

  private final SimpMessagingTemplate simpMessagingTemplate;
  
  @PostConstruct
  void init() {
    log.info("FakeController started.");
  }
  
  @GetMapping
  public String batchsPage(final Model model, HttpServletRequest request, HttpServletResponse response) {
     log.debug("start page");
     
     model.addAttribute("environment", EnvironmentCurrentController.INSTANCE.getCurrent());
     model.addAttribute("environments", Environment.values());
     
     return Pages.MONITORING.pageName;
  }
  
  @PostMapping(value = Mapping.Fake.CHANGE_ENVIRONMENT)
  public ResponseEntity<Void> achangeStatusAjax(@PathVariable("env") @NotNull Integer envOrnial) {
    
    Environment environmentTo = Environment.values()[envOrnial];
    
    log.info("Environment change from: {} to {}", EnvironmentCurrentController.INSTANCE.getCurrent(), environmentTo);
    
    EnvironmentCurrentController.INSTANCE.setCurrent(environmentTo);
    
    simpMessagingTemplate.convertAndSend("/topic/environment",  environmentTo);
    
    return ResponseEntity.ok().build();
  }
}