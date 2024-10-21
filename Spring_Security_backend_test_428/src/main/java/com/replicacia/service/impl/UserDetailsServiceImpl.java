package com.replicacia.service.impl;

import com.replicacia.model.AppUser;
import com.replicacia.model.Permission;
import com.replicacia.model.security.UserPrincipal;
import com.replicacia.repo.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repo;

	@Value("${login.maxretry}")
	private int maxretry;
	@Value("${login.maxretrycaptcha}")
	private int maxretrycaptcha;


	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String username) throws AuthenticationException {
		final AppUser appUser = this.repo.findByUsernameAndActiveTrue(username);
		if (appUser != null) {
			this.checkMaxLoginRetries(appUser);
			return new UserPrincipal(appUser);
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}


	/**
	 * decrement max retry count by 1 for each unsucessfull login
	 * @param username
	 */
	public void updateMaxRetry(final String username){
		this.repo.updateMaxRetryCount(username);
	}


	private void checkMaxLoginRetries(final AppUser appUser) throws LockedException {
		if(appUser.getLoginAttempts() == 0){
			throw new LockedException("User exceeded max retry. Account locked " + appUser.getUsername());
		}
		//TODO: Add logic later
	}

	private static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Permission> roles) {
		final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (final Permission role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getApi().toUpperCase()+"_"+role.getApiAccess().toUpperCase()));
		}
		return authorities;
	}
}