package com.replicacia.service;

import com.replicacia.rest.security.enums.OtpType;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface OTPNotificationService {
  ResponseEntity<String> sendOtp(final Map<OtpType, String> reqMap);

  ResponseEntity<String> verifyOtp(final Map<String, Integer> reqMap);
}
