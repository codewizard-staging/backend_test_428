package com.replicacia.model;

import com.replicacia.utils.DBUtils;
import java.io.Serializable;
import javax.persistence.*;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, updatable = false)
  private String publicId;

  @PrePersist
  protected void onCreate() {
    this.publicId = DBUtils.generatePublicId();
  }

  public String getPublicId() {
    return this.publicId;
  }

  public void setPublicId(final String publicId) {
    this.publicId = publicId;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }
}
