package com.pranavaeet.astro.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pranavaeet.astro.entity.AuthRequest;
import com.pranavaeet.astro.entity.UserInfo;
import com.pranavaeet.astro.service.JwtService;
import com.pranavaeet.astro.service.UserInfoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder encoder;

	@GetMapping("/welcome")
	public String welcome() {
		return "welcome";
	}
	
	@PostMapping("/addnewuser")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<UserInfo> addNewUser(@RequestBody UserInfo userInfo) {
		Map<String, Object> data = new HashMap<>();
			// UserInfo userData = new UserInfo();
			userInfo.setPassword(encoder.encode(userInfo.getPassword()));
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			userInfo.setCreatedby(authentication.getName());
			userInfoService.addUser(userInfo);

		return new ResponseEntity<UserInfo>(userInfo, HttpStatus.OK);
	}
	@PostMapping("/processsignup")
	public ResponseEntity<Object> processSignUp(@RequestBody UserInfo userInfo) {
		Map<String, Object> data = new HashMap<>();
			UserInfo userData = new UserInfo();
			userData.setPassword(encoder.encode(userInfo.getPassword()));
			data.put("status", "0");
			data.put("message", userInfoService.addUser(userData));
			data.put("user", userData);
			data.put("userexists", true);
			data.put("status", "-1");
			data.put("message", "Sorry OTP Does not match");
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	@GetMapping("/user/userprofile")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String userProfile() {
		return "Welcome to User Profile";
	}

	@GetMapping("/admin/adminprofile")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String adminProfile() {
		return "Welcome to Admin Profile";
	}

	@PostMapping("/generatetoken")
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		if (authentication.isAuthenticated()) {
			return jwtService.generateToken(authRequest.getUsername());
		} else {
			throw new UsernameNotFoundException("invalid user request !");
		}
	}
	@PostMapping("/signin")
	public ResponseEntity<Object> signin(@RequestBody UserInfo userInfo) {
		Map<String, Object> data = new HashMap<>();
		userInfoService.save(userInfo);
		HashMap<String, String> output = new HashMap<String, String>();
		data.putAll(output);
		data.put("userInfo", true);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@GetMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		Cookie deleteServletCookie = new Cookie("jwt", null);
		deleteServletCookie.setMaxAge(0);
		deleteServletCookie.setSecure(true);
		deleteServletCookie.setHttpOnly(true);
		deleteServletCookie.setPath("/");
		response.addCookie(deleteServletCookie);
		HttpSession session = request.getSession();
		session.invalidate();
		response.setStatus(HttpServletResponse.SC_FOUND);
		response.setHeader("Location", "/login");
	}

}
