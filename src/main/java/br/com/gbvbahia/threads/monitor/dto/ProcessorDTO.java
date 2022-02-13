package br.com.gbvbahia.threads.monitor.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorDTO extends BaseDTO {

  private static final long serialVersionUID = 1L;

  @Size(min= 0, max= 255)
  @NotNull(message = "name required")
  private String name;
  
  @Size(min= 0, max= 1000)
  @NotNull(message = "data to process required")
  private String dataToProcess;
  
  @Size(min= 0, max= 2048)
  @NotNull(message = "url to call required")
  private String urlToCall;
  
  private ProcessStatus processStatus;
}
