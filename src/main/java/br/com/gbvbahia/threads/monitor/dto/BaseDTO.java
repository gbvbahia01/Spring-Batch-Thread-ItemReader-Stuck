package br.com.gbvbahia.threads.monitor.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BaseDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private Long id;
  protected LocalDateTime createdAt;
  protected LocalDateTime updatedAt;
  
}