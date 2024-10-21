package com.replicacia.controller;

import com.replicacia.exception.ResourceNotFoundException;
import com.replicacia.model.AppUser;
import com.replicacia.rest.security.enums.OtpType;
import com.replicacia.rest.security.service.UserService;
import com.replicacia.service.OTPNotificationService;
import com.replicacia.service.ProxyService;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/gateway")
public class ProxyController {

  private final ProxyService proxyService;
  private final OTPNotificationService otpNotificationService;
  private final UserService userService;

  @RequestMapping("/**")
  public ResponseEntity<String> handle(
      @RequestBody(required = false) final String body,
      final HttpMethod method,
      final HttpServletRequest request,
      final HttpServletResponse response)
      throws URISyntaxException {
    //
    return this.proxyService.processProxyRequest(
        body, method, request, response, UUID.randomUUID().toString());
  }

  @PostMapping("/app/otp/send")
  public ResponseEntity<String> generateOtpProxy(@RequestBody final Map<OtpType, String> reqMap) {

    reqMap.forEach(
        (otpType, value) -> {
          if (OtpType.EMAIL.equals(otpType)) {
            final Optional<AppUser> byEmail = this.userService.findByEmail(value);
            if (!byEmail.isPresent()) {
              throw new ResourceNotFoundException(
                  "Email id: " + value + " is not registered with us.");
            }
          }

          if (OtpType.SMS.equals(otpType)) {
            final Optional<AppUser> byMobileNumber = this.userService.findByMobileNumber(value);
            if (!byMobileNumber.isPresent()) {
              throw new ResourceNotFoundException(
                  "Mobile Number: " + value + " is not registered with us.");
            }
          }
        });

    return this.otpNotificationService.sendOtp(reqMap);
  }

  private void accept(final OtpType otpType, final String value) {
    if (OtpType.EMAIL.equals(otpType)) {
      final Optional<AppUser> byEmail = this.userService.findByEmail(value);
    }

    if (OtpType.SMS.equals(otpType)) {
      this.userService.findByMobileNumber(value);
    }
  }
}
