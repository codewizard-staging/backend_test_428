package com.replicacia.rest.security.dto;

import com.replicacia.rest.security.enums.OtpType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartUserVerificationReqDTO {
  private List<OtpType> types;
}
