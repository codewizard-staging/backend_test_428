package com.replicacia.rest.security.service.impl;

import com.replicacia.exception.BadRequestException;
import com.replicacia.exception.ResourceExistsException;
import com.replicacia.model.AppUser;
import com.replicacia.model.Role;
import com.replicacia.repo.RoleRepository;
import com.replicacia.repo.UserRepository;
import com.replicacia.rest.security.dto.ForgotPasswordReqDTO;
import com.replicacia.rest.security.dto.OtpVerifyDTO;
import com.replicacia.rest.security.dto.StartUserVerificationReqDTO;
import com.replicacia.rest.security.dto.UserRegisterRequestDTO;
import com.replicacia.rest.security.dto.UserRegisterResponseDTO;
import com.replicacia.rest.security.enums.OtpType;
import com.replicacia.rest.security.service.UserService;
import com.replicacia.service.OTPNotificationService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ModelMapper mapper;

  @Autowired private RoleRepository roleRepository;

  @Autowired private OTPNotificationService otpNotificationService;

  @Value("${login.maxretry}")
  private int maxRetry;

  @Autowired private RestTemplate restTemplate;

  /*
   * save user.
   */
  @Override
  @Transactional
  public AppUser save(final AppUser appUser, final Boolean setDefaultRole) {
    this.setNewUserData(appUser);
    final Optional<AppUser> optionalAppUser =
        this.userRepository.findByUsername(appUser.getUsername());
    if (optionalAppUser.isPresent()) {
      throw new ResourceExistsException(
          "Username: " + appUser.getUsername() + " is not available. Please use another");
    }
    final Optional<AppUser> optionalAppUser1 = this.userRepository.findByEmail(appUser.getEmail());

    if (optionalAppUser1.isPresent()) {
      throw new ResourceExistsException(
          "EmailId: "
              + appUser.getEmail()
              + " is already registered with us. Please use alternate EmailId");
    }

    final Optional<AppUser> optionalAppUserByMobile =
        this.userRepository.findByMobileNumber(appUser.getMobileNumber());
    if (optionalAppUserByMobile.isPresent()) {
      throw new ResourceExistsException(
          "Mobile Number: "
              + appUser.getMobileNumber()
              + " is already registered with us. Please use alternate Mobile Number.");
    }
    if (setDefaultRole) {
      final Optional<Role> role = this.roleRepository.findByName("PARENT");
      if (role.isPresent() && Objects.nonNull(appUser.getRoles())) {
        appUser.getRoles().add(role.get());
      }
    }

    return this.userRepository.save(appUser);
  }

  /*
   * update user.
   */
  @Override
  public AppUser update(final AppUser appUser) {
    if (appUser.getPasswordChanged()) {
      appUser.setPassword(this.passwordEncoder.encode(appUser.getPassword()));
    }
    // appUser.setSuperUser(false);
    final AppUser user = this.userRepository.save(appUser);
    // sendNotification(appUser,"update");
    return user;
  }

  /**
   * set details for new users
   *
   * @param appUser
   */
  private void setNewUserData(final AppUser appUser) {
    if (appUser.getId() == null) {
      appUser.setLoginAttempts(this.maxRetry);
      appUser.setPassword(this.passwordEncoder.encode(appUser.getPassword()));
      appUser.setIsEmailVerified(Boolean.FALSE);
      appUser.setActive(Boolean.FALSE);
    }
  }

  /** find all users by active status */
  @Override
  public List<AppUser> findAllByActive(final Boolean active) {
    return this.userRepository.findByActiveIs(active);
  }

  @Override
  public AppUser findAllByUserName(final String userName) {
    return this.userRepository.findByUsername(userName).orElse(null);
  }

  @Override
  public Optional<AppUser> findById(final Long userId) {
    return this.userRepository.findById(userId);
  }

  @Override
  public Optional<AppUser> findByPublicId(final String publicId) {
    return this.userRepository.findByPublicId(publicId);
  }

  @Override
  public Optional<AppUser> findByUsername(final String username) {
    return this.userRepository.findByUsername(username);
  }

  @Override
  public Optional<AppUser> findByEmail(final String email) {
    return this.userRepository.findByEmail(email);
  }

  @Override
  public UserRegisterResponseDTO register(final UserRegisterRequestDTO userRegisterRequestDTO) {
    final AppUser appUser = this.mapper.map(userRegisterRequestDTO, AppUser.class);
    final AppUser savedUser = this.save(appUser, true);
    return this.mapper.map(savedUser, UserRegisterResponseDTO.class);
  }

  @Override
  public ResponseEntity<String> forgotPassword(final ForgotPasswordReqDTO dto) {
    final Map<OtpType, String> reqMap = new HashMap<>();
    reqMap.put(dto.getType(), dto.getValue());
    return this.otpNotificationService.sendOtp(reqMap);
  }

  @Override
  public void delete(final String email) {}

  @Override
  public void addRolesToUser(final String publicId, final List<String> roleIds) {
    final Optional<AppUser> userOptional = this.userRepository.findByPublicId(publicId);
    if (userOptional.isPresent()) {
      final List<Role> roleSet = this.roleRepository.findByPublicIdIn(roleIds);
      final AppUser user = userOptional.get();

      if (Objects.nonNull(user.getRoles())) {
        user.getRoles().addAll(roleSet);
      } else {
        user.setRoles(new HashSet<>(roleSet));
      }

      this.userRepository.save(user);
    }
  }

  @Override
  public void removeRolesToUser(final String publicId, final List<String> roleIds) {
    final Optional<AppUser> userOptional = this.userRepository.findByPublicId(publicId);
    if (userOptional.isPresent()) {
      final List<Role> roleSet = this.roleRepository.findByPublicIdIn(roleIds);
      final AppUser user = userOptional.get();

      if (Objects.nonNull(user.getRoles())) {
        roleSet.forEach(user.getRoles()::remove);
      }

      this.userRepository.save(user);
    }
  }

  @Override
  public Set<Role> getAllUserRoles(final String publicId) {
    final Optional<AppUser> optionalAppUser = this.userRepository.findByPublicId(publicId);
    return optionalAppUser.map(AppUser::getRoles).orElse(new HashSet<>());
  }

  @Override
  public void verifyUser(final AppUser appUser, final OtpVerifyDTO dto, final Boolean verifyOtp) {
    if (verifyOtp && !this.verifyOtp(appUser, dto)) {
      throw new BadRequestException(
          "Otp verification is failed. Please try again after some time.");
    }

    appUser.setIsEmailVerified(Boolean.TRUE);
    appUser.setActive(Boolean.TRUE);
    this.update(appUser);
  }

  @Override
  public Optional<AppUser> findByMobileNumber(final String value) {
    return this.userRepository.findByMobileNumber(value);
  }

  @Override
  public Boolean verifyOtp(final AppUser appUser, final OtpVerifyDTO dto) {
    final Map<String, Integer> otpVerifyReq = new HashMap<>();
    if (OtpType.SMS.equals(dto.getType())) {
      otpVerifyReq.put(appUser.getMobileNumber(), dto.getOtp());
    } else {
      otpVerifyReq.put(appUser.getEmail(), dto.getOtp());
    }
    final ResponseEntity<String> response = this.otpNotificationService.verifyOtp(otpVerifyReq);

    return Boolean.valueOf(response.getBody());
  }

  @Override
  public void startUserVerification(final AppUser user, final StartUserVerificationReqDTO dto) {
    final Map<OtpType, String> reqMap = new HashMap<>();
    dto.getTypes()
        .forEach(
            type -> {
              switch (type) {
                case SMS:
                  reqMap.put(type, user.getMobileNumber());
                  break;
                case EMAIL:
                  reqMap.put(type, user.getEmail());
                  break;
                default:
                  throw new BadRequestException("Invalid OTP Type");
              }
            });
    final ResponseEntity<String> response = this.otpNotificationService.sendOtp(reqMap);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new BadRequestException("Some went wrong: " + response.getBody());
    }
  }
}
