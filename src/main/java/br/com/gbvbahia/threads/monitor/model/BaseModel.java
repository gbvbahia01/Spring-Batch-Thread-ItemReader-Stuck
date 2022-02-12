package br.com.gbvbahia.threads.monitor.model;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public class BaseModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name= "created_at", updatable = false)
  protected LocalDateTime createdAt;
  
  @Column(name= "updated_at")
  protected LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
      this.createdAt = LocalDateTime.now();
      this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
      this.updatedAt = LocalDateTime.now();
  }
  
  public boolean isNew() {

    return this.id == null;
  }
}