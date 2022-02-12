package br.com.gbvbahia.threads.monitor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  
  @Size(min= 0, max= 255)
  @NotNull(message = "bearer token required")
  @Column(name = "bearer_token")
  private String bearerToken;
  
  @Size(min= 0, max= 2048)
  @NotNull(message = "url to call required")
  @Column(name = "url_to_call")
  private String urlToCall;
}
