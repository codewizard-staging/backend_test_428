package com.replicacia.rest.security.dto;

import com.replicacia.rest.security.enums.OtpType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyDTO {
  private Integer otp;
  private OtpType type;
}
