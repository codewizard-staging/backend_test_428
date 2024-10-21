package com.replicacia.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The persistent class for the permission database table. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Permission extends BaseEntity {

  private String name;

  private String api;

  private String apiAccess;

  @Override
  public int hashCode() {
    return Objects.hash(this.api, this.apiAccess);
  }

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(
      name = "role_permission",
      joinColumns = @JoinColumn(name = "permission_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  public Permission(final String name) {
    this.name = name;
  }

  public Permission(final String name, final String api, final String apiAccess) {
    this.name = name;
    this.api = api;
    this.apiAccess = apiAccess;
  }

  public Permission(final String api, final String apiAccess) {
    this.api = api;
    this.apiAccess = apiAccess;
  }

  @Override
  public boolean equals(final Object obj) {
    final String api = ((Permission) obj).api;
    final Pattern pattern = Pattern.compile(this.api);
    return pattern.matcher(api).matches()
        && this.apiAccess.equalsIgnoreCase(((Permission) obj).apiAccess);
  }

  public Set<Role> getRoles() {
    if (Objects.isNull(this.roles)) {
      return new HashSet<>();
    }
    return this.roles;
  }
}
