package com.replicacia.rest.security.service;

import com.replicacia.model.AppUser;
import com.replicacia.model.Role;
import com.replicacia.rest.security.dto.ForgotPasswordReqDTO;
import com.replicacia.rest.security.dto.OtpVerifyDTO;
import com.replicacia.rest.security.dto.StartUserVerificationReqDTO;
import com.replicacia.rest.security.dto.UserRegisterRequestDTO;
import com.replicacia.rest.security.dto.UserRegisterResponseDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.ResponseEntity;

public interface UserService {
  AppUser save(AppUser user, Boolean setDefaultRole);

  AppUser update(AppUser user);

  List<AppUser> findAllByActive(Boolean active);

  AppUser findAllByUserName(String userName);

  Optional<AppUser> findById(Long userId);

  Optional<AppUser> findByPublicId(String publicId);

  Optional<AppUser> findByUsername(String username);

  Optional<AppUser> findByEmail(String email);

  UserRegisterResponseDTO register(UserRegisterRequestDTO userRegisterRequestDTO);

  ResponseEntity<String> forgotPassword(ForgotPasswordReqDTO dto);

  void delete(String email);

  void addRolesToUser(String publicId, List<String> roleIds);

  void removeRolesToUser(String publicId, List<String> roleIds);

  Set<Role> getAllUserRoles(String publicId);

  void verifyUser(AppUser appUser, OtpVerifyDTO dto, Boolean verifyOtp);

  Optional<AppUser> findByMobileNumber(String value);

  Boolean verifyOtp(AppUser appUser, OtpVerifyDTO dto);

  void startUserVerification(AppUser user, StartUserVerificationReqDTO dto);
}
