package com.replicacia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppUser extends BaseEntity {

  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private String name;

  private String mobileNumber;

  private Boolean active;

  private String email;

  @JsonIgnore private Boolean superUser = false;

  @JsonIgnore private Integer loginAttempts;

  @JsonIgnore private Boolean resolveCaptcha = false;

  @Transient private String captcha; // captcha entered from UI.

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Boolean passwordChanged = false; // send as true when password changed.

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "appuser_role",
      joinColumns = @JoinColumn(name = "appuser_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @EqualsAndHashCode.Exclude
  private Set<Role> roles = new HashSet<>();

  private String country;

  private Boolean isEmailVerified;

  public AppUser(final String username, final String password) {
    this.username = username;
    this.password = password;
  }
}
