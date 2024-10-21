package com.replicacia.rest.security.dto;

import com.replicacia.rest.security.enums.OtpType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordReqDTO {
  private OtpType type;
  private String value;
}
