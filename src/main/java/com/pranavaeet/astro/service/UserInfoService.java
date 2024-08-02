package com.pranavaeet.astro.service;

import java.util.*;


import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.entity.UserInfo;
import com.pranavaeet.astro.repository.RoleRepository;
import com.pranavaeet.astro.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import javax.validation.Valid;

@Service
public class UserInfoService implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo user=repository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username "+username+" not found"));
		return new User(user.getUsername(),user.getPassword(),mapRolesToAuthorities(user.getRoles()));
	}
	private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
		return roles.stream().map(role->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	public UserInfo addUser(UserInfo userInfo) {
		Role defaultRole = roleService.findByName("ROLE_USER");
		if (defaultRole != null && 	userInfo.getRoles().isEmpty()) {
			userInfo.addRole(defaultRole);
		}
		repository.save(userInfo);
		return userInfo;
	}

	public Optional<UserInfo> findByUsername(String name) {
		return repository.findByUsername(name);
	}

	public @Valid UserInfo save(@Valid UserInfo user) {
		return repository.save(user);
	}

	public Optional<UserInfo> findById(long id) {
		return repository.findById(id);
	}

	public Page<UserInfo> findPaginated(String keyword, int size, int page) {
		Pageable pageable = Pageable.unpaged();
		if (size != -1)
			pageable = PageRequest.of(page - 1, size);
		String key = keyword == null ? "" : keyword;
		if (key.isEmpty()) {
			return repository.findAll(pageable);
		}
		return repository.findAll(pageable);
	}
	
	public String deleteById(long id) {
		UserInfo user = repository.findById(id).orElse(null);
		if (user != null) {
			// Detach roles from user
			user.getRoles().clear();
			repository.deleteById(id);
		}
		return "Successfully Deleted";
	}

	public boolean usernameExists(String username) {
		return repository.findByUsername(username).isPresent();
	}

	public List<String> findAllUsernames() {
		return repository.findAllUsernames();
	}
}
