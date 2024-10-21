package com.replicacia.rest.security;

import com.replicacia.exception.BadRequestException;
import com.replicacia.exception.ResourceNotFoundException;
import com.replicacia.model.AppUser;
import com.replicacia.model.Role;
import com.replicacia.rest.admin.dto.AssignRoleRequestDTO;
import com.replicacia.rest.admin.dto.RoleResponseDTO;
import com.replicacia.rest.security.dto.ForgotPasswordReqDTO;
import com.replicacia.rest.security.dto.OtpVerifyDTO;
import com.replicacia.rest.security.dto.StartUserVerificationReqDTO;
import com.replicacia.rest.security.dto.UpdatePasswordRequestDTO;
import com.replicacia.rest.security.dto.UserResponseDTO;
import com.replicacia.rest.security.enums.OtpType;
import com.replicacia.rest.security.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/app/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final ModelMapper mapper;

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponseDTO> getUserByUserId(
      @PathVariable(value = "userId") final String userId) {
    final AppUser appUser =
        this.userService
            .findByPublicId(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("User with ID :" + userId + " Not Found!"));
    if (!appUser.getActive()) {
      throw new ResourceNotFoundException("User with ID :" + userId + " is Inactive!");
    }
    return ResponseEntity.ok().body(this.mapper.map(appUser, UserResponseDTO.class));
  }

  @PostMapping("/{id}/start-verifications")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void startUserVerification(
      @PathVariable final String id, @RequestBody final StartUserVerificationReqDTO dto) {
    final Optional<AppUser> appUser = this.userService.findByPublicId(id);
    appUser.ifPresent(user -> this.userService.startUserVerification(user, dto));
  }

  @PutMapping("/{id}/verify")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void updateVerifiedUserFlag(
      @PathVariable final String id, @RequestBody final OtpVerifyDTO dto) {
    final Optional<AppUser> appUser = this.userService.findByPublicId(id);
    appUser.ifPresent(user -> this.userService.verifyUser(user, dto, true));
  }

  @PutMapping("/forgotPassword")
  public ResponseEntity<UserResponseDTO> forgotPassword(
      @RequestBody final ForgotPasswordReqDTO dto) {
    Optional<AppUser> appUser = Optional.empty();
    if (OtpType.EMAIL.equals(dto.getType())) {
      appUser = this.userService.findByEmail(dto.getValue());
    } else if (OtpType.SMS.equals(dto.getType())) {
      appUser = this.userService.findByMobileNumber(dto.getValue());
    }
    if (appUser.isPresent()) {
      final ResponseEntity<String> res = this.userService.forgotPassword(dto);
      if (res.getStatusCode().is2xxSuccessful()) {
        return ResponseEntity.ok().body(this.mapper.map(appUser, UserResponseDTO.class));
      }
    } else {
      throw new ResourceNotFoundException("Email or phone is not registered with us.");
    }

    return null;
  }

  @PutMapping("/{id}/updatePassword")
  public ResponseEntity<?> updatePassword(
      @PathVariable final String id, @RequestBody final UpdatePasswordRequestDTO requestDTO) {
    final Optional<AppUser> appUserOptional = this.userService.findByPublicId(id);
    if (appUserOptional.isPresent()
        && requestDTO.getNewPassword().equals(requestDTO.getVerifyPassword())
        && this.userService.verifyOtp(
            appUserOptional.get(),
            OtpVerifyDTO.builder().otp(requestDTO.getOtp()).type(requestDTO.getType()).build())) {

      final AppUser user = appUserOptional.get();
      user.setPasswordChanged(true);
      user.setPassword(requestDTO.getNewPassword());
      this.userService.update(user);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              UserResponseDTO.builder()
                  .userId(user.getPublicId())
                  .active(user.getActive())
                  .email(user.getEmail())
                  .isEmailVerified(user.getIsEmailVerified())
                  .country(user.getCountry())
                  .userName(user.getUsername())
                  .build());
    } else {
      throw new BadRequestException("Something went wrong. Please try again After some time.");
    }
  }

  @DeleteMapping("/{email}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deativateUser(@PathVariable final String email) {
    this.userService.delete(email);
  }

  @GetMapping
  public ResponseEntity<UserResponseDTO> getUserByEmailId(
      @RequestParam(value = "email") final String email) {
    final AppUser appUser =
        this.userService
            .findByEmail(email)
            .orElseThrow(
                () -> new ResourceNotFoundException("User with ID :" + email + " Not Found!"));

    if (!appUser.getActive()) {
      throw new ResourceNotFoundException("User with ID :" + email + " is Inactive!");
    }

    return ResponseEntity.ok().body(this.mapper.map(appUser, UserResponseDTO.class));
  }

  @PutMapping("/{id}/roles")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addUserRoles(
      @PathVariable final String id, @RequestBody final AssignRoleRequestDTO requestDTO) {
    this.userService.addRolesToUser(id, requestDTO.getRoleIds());
  }

  @DeleteMapping("/{id}/roles")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeUserRoles(
      @PathVariable final String id, @RequestBody final AssignRoleRequestDTO requestDTO) {
    this.userService.removeRolesToUser(id, requestDTO.getRoleIds());
  }

  @GetMapping("/{id}/roles")
  @ResponseStatus(HttpStatus.OK)
  public List<RoleResponseDTO> getUserRoles(@PathVariable final String id) {
    final Set<Role> roles = this.userService.getAllUserRoles(id);

    return roles.stream()
        .map(role -> this.mapper.map(role, RoleResponseDTO.class))
        .collect(Collectors.toList());
  }
}
