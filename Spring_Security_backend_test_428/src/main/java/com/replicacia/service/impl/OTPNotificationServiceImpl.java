package com.replicacia.service.impl;

import com.replicacia.rest.security.enums.OtpType;
import com.replicacia.service.OTPNotificationService;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OTPNotificationServiceImpl implements OTPNotificationService {
  private final RestTemplate restTemplate;

  @Value("${otp.service.host}")
  private String otpServiceHost;

  @Value("${otp.service.send_api}")
  private String otpServiceSendApi;

  @Value("${otp.service.verify_api}")
  private String otpServiceVerifyApi;

  @Override
  public ResponseEntity<String> sendOtp(final Map<OtpType, String> reqMap) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final HttpEntity<Map<OtpType, String>> httpEntity = new HttpEntity<>(reqMap, headers);

    return this.restTemplate.exchange(
        URI.create(this.otpServiceHost + this.otpServiceSendApi),
        HttpMethod.POST,
        httpEntity,
        String.class);
  }

  @Override
  public ResponseEntity<String> verifyOtp(final Map<String, Integer> reqMap) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final HttpEntity<Map<String, Integer>> httpEntity = new HttpEntity<>(reqMap, headers);

    return this.restTemplate.exchange(
        URI.create(this.otpServiceHost + this.otpServiceVerifyApi),
        HttpMethod.POST,
        httpEntity,
        String.class);
  }
}
