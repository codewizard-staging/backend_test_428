package com.replicacia.config.security;

import com.replicacia.model.AppUser;
import com.replicacia.model.security.UserPrincipal;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    protected final Authentication authentication;
    private AuthenticationTrustResolver trustResolver;
    private RoleHierarchy roleHierarchy;
    private Set<String> roles;
    private String defaultRolePrefix = "ROLE_";

    public final boolean permitAll = true;
    public final boolean denyAll = false;
    private PermissionEvaluator permissionEvaluator;
    public final String read = "READ";
    public final String write = "WRITE";
    public final String update = "UPDATE";
    public final String delete = "DELETE";
    public final String admin = "ADMIN";

    private Object filterObject;
    private Object returnObject;

    public SecurityExpressionRoot(final Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication object cannot be null");
        }
        this.authentication = authentication;
    }

    @Override
    public final boolean hasAuthority(final String authority) {
        final AppUser user = ((UserPrincipal) this.getPrincipal()).getUser();
        if(user.getSuperUser()){
            return true;

        }
        final Set<String> roleSet = this.getAuthoritySet();
        if (roleSet!=null && !roleSet.isEmpty()){
            return roleSet.contains(authority);
        }
        return false;
    }

    public boolean updateOwnRecord(final Long id) {
        final AppUser user = ((UserPrincipal) this.getPrincipal()).getUser();
        return (user.getSuperUser() || user.getId() == id.longValue());
    }

    @Override
    public final boolean hasAnyAuthority(final String... authorities) {
        return this.hasAnyAuthorityName(null, authorities);
    }

    @Override
    public final boolean hasRole(final String role) {
        return this.hasAnyRole(role);
    }

    @Override
    public final boolean hasAnyRole(final String... roles) {
        return this.hasAnyAuthorityName(this.defaultRolePrefix, roles);
    }

    private boolean hasAnyAuthorityName(final String prefix, final String... roles) {
        final Set<String> roleSet = this.getAuthoritySet();

        for (final String role : roles) {
            final String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
            if (roleSet.contains(defaultedRole)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final Authentication getAuthentication() {
        return this.authentication;
    }

    @Override
    public final boolean permitAll() {
        return true;
    }

    @Override
    public final boolean denyAll() {
        return false;
    }

    @Override
    public final boolean isAnonymous() {
        return this.trustResolver.isAnonymous(this.authentication);
    }

    @Override
    public final boolean isAuthenticated() {
        return !this.isAnonymous();
    }

    @Override
    public final boolean isRememberMe() {
        return this.trustResolver.isRememberMe(this.authentication);
    }

    @Override
    public final boolean isFullyAuthenticated() {
        return !this.trustResolver.isAnonymous(this.authentication) && !this.trustResolver.isRememberMe(
            this.authentication);
    }

    public Object getPrincipal() {
        return this.authentication.getPrincipal();
    }

    public void setTrustResolver(final AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    public void setRoleHierarchy(final RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    public void setDefaultRolePrefix(final String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }

    private Set<String> getAuthoritySet() {
        if (this.roles == null) {
            Collection<? extends GrantedAuthority> userAuthorities = this.authentication.getAuthorities();

            if (this.roleHierarchy != null) {
                userAuthorities = this.roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
            }

            this.roles = AuthorityUtils.authorityListToSet(userAuthorities);
        }

        return this.roles;
    }

    @Override
    public boolean hasPermission(final Object target, final Object permission) {
        return this.permissionEvaluator.hasPermission(this.authentication, target, permission);
    }

    @Override
    public boolean hasPermission(final Object targetId, final String targetType, final Object permission) {
        return this.permissionEvaluator.hasPermission(this.authentication, (Serializable) targetId, targetType, permission);
    }

    public void setPermissionEvaluator(final PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    private static String getRoleWithDefaultPrefix(final String defaultRolePrefix, final String role) {
        if (role == null) {
            return role;
        }
        if ((defaultRolePrefix == null) || (defaultRolePrefix.length() == 0)) {
            return role;
        }
        if (role.startsWith(defaultRolePrefix)) {
            return role;
        }
        return defaultRolePrefix + role;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(final Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(final Object obj) {
        this.returnObject = obj;
    }
}