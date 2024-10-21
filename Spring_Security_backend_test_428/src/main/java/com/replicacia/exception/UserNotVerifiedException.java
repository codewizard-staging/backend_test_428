package com.replicacia.exception;

import com.replicacia.rest.security.enums.ErrorEnum;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
@Builder
@Getter
@Setter
public class UserNotVerifiedException extends RuntimeException {
  private List<ErrorEnum> reasons;
  private String userId;
}
