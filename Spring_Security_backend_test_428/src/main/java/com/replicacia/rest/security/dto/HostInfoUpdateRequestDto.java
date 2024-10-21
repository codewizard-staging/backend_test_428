package com.replicacia.rest.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostInfoUpdateRequestDto {
  private String id;
  private String ip;
  private Integer port;
  private String scheme;
  private String serviceName;
  private String name;
}
