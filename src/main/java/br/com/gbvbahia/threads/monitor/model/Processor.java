package br.com.gbvbahia.threads.monitor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "processor")
public class Processor extends BaseModel {

  private static final long serialVersionUID = 1L;

  @Size(min= 0, max= 255)
  @NotNull(message = "name required")
  private String name;
  
  @Size(min= 0, max= 1000)
  @NotNull(message = "data to process required")
  @Column(name = "data_to_process")
  private String dataToProcess;

  @Size(min= 0, max= 255)
  @Column(name = "data_result")
  private String dataResult;
  
  @Size(min= 0, max= 2048)
  @NotNull(message = "url to call required")
  @Column(name = "url_to_call")
  private String urlToCall;
  
  @NotNull(message = "process status required")
  @Column(name = "process_status")
  @Enumerated(EnumType.STRING)
  private ProcessStatus processStatus;
  
  @Size(min= 0, max= 255)
  @Column(name = "process_result")
  private String processResult;
  
}
