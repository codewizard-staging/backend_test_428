package com.replicacia.rest.security.dto;

import com.replicacia.rest.security.enums.ErrorEnum;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserNotVerifiedResponseDTO {
  private List<ErrorEnum> reasons;
  private String userId;
}
