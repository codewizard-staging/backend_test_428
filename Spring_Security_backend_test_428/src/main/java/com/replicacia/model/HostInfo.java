package com.replicacia.model;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "HostInfo")
@NoArgsConstructor
@AllArgsConstructor
public class HostInfo extends BaseEntity {

  private String serviceName;
  private String name;
  private String scheme;
  private String ip;
  private Integer port;
}
