package com.replicacia.rest.security.service.impl;

import com.replicacia.exception.UserNotVerifiedException;
import com.replicacia.model.AppUser;
import com.replicacia.rest.security.dto.LoginRequestDTO;
import com.replicacia.rest.security.dto.LoginResponseDTO;
import com.replicacia.rest.security.enums.ErrorEnum;
import com.replicacia.rest.security.service.LoginService;
import com.replicacia.rest.security.service.UserService;
import com.replicacia.rest.security.util.JwtTokenUtil;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
@CrossOrigin
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserService userService;

  @Override
  public LoginResponseDTO login(final LoginRequestDTO loginRequestDTO) {
    final AppUser appUser = this.userService.findAllByUserName(loginRequestDTO.getUserName());

    if (Objects.nonNull(appUser) && !appUser.getActive() && !appUser.getIsEmailVerified()) {
      throw UserNotVerifiedException.builder()
          .userId(appUser.getPublicId())
          .reasons(Collections.singletonList(ErrorEnum.NOT_VERIFIED))
          .build();
    }

    final Authentication authentication =
        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUserName(), loginRequestDTO.getPassword()));

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String token = this.jwtTokenUtil.generateToken(userDetails);

    return LoginResponseDTO.builder().type("Bearer").token(token).build();
  }
}
