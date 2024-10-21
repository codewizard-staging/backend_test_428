package com.replicacia.config.security;

import com.replicacia.model.AppUser;
import com.replicacia.model.Permission;
import com.replicacia.model.security.UserPrincipal;
import com.replicacia.rest.security.util.JwtTokenUtil;
import com.replicacia.service.impl.UserDetailsServiceImpl;
import com.replicacia.web.ApiError;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleBasedAuthorizationFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl jwtUserDetailsService;
  private final JwtTokenUtil jwtTokenUtil;

  private static final Set<String> skipAuthApis =
      new HashSet<>(
          Arrays.asList(
              "/app/login",
              "/app/signup",
              "/app/signup",
              "/app/users/forgotPassword",
              "/app/users/\\d+/updatePassword",
              "/app/users/\\d+/verify",
              "/gateway/app/otp/send",
              "/app/users/\\d+/start-verifications"));

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    if (!StringUtils.isEmpty(request.getRequestURI())
        && skipAuthApis.stream()
            .noneMatch(s -> Pattern.compile(s).matcher(request.getRequestURI()).matches())) {

      AppUser appUser = null;
      final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      String username = null;
      String jwtToken = null;
      if (requestTokenHeader != null) {
        if (requestTokenHeader.startsWith("Bearer ")) {
          jwtToken = requestTokenHeader.substring(7);
          username = this.jwtTokenUtil.getUsernameFromToken(jwtToken);
        }
      } else {
        log.error("Token is missing");
        this.setErrorResponse(response, "Bearer Token is missing");
        return;
      }
      if (username != null) {
        appUser =
            ((UserPrincipal) this.jwtUserDetailsService.loadUserByUsername(username)).getUser();
      }

      final String requestUrl = request.getRequestURI().replaceFirst("/gateway", "");

      if (!this.isUserAuthorized(appUser, request)) {
        log.error("User not authorized to access API: " + requestUrl);
        this.setErrorResponse(response, "User not authorized to access API: " + requestUrl);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private boolean isUserAuthorized(final AppUser appUser, final HttpServletRequest request) {
    final String api = request.getRequestURI().replaceFirst("/gateway", "");
    final String method = request.getMethod();
    final Set<Permission> roles =
        Objects.nonNull(appUser) && Objects.nonNull(appUser.getRoles())
            ? appUser.getRoles().stream()
                .flatMap(roleGroup -> roleGroup.getPermissions().stream())
                .collect(Collectors.toSet())
            : Collections.EMPTY_SET;
    return roles.stream().anyMatch(role -> role.equals(new Permission(api, method)));
  }

  private void setErrorResponse(final HttpServletResponse response, final String message) {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(ContentType.APPLICATION_JSON.toString());

    final List<String> details = new ArrayList<String>();
    details.add(message);

    final ApiError err =
        new ApiError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED, "Authorization failed", details);
    try {
      final String json = err.convertToJson();
      response.getWriter().write(json);
    } catch (final IOException e) {
      log.error("Exception in parsing ApiError", e);
    }
  }
}
