package com.pranavaeet.astro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranavaeet.astro.service.JwtService;
import com.pranavaeet.astro.service.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;

import org.checkerframework.checker.regex.qual.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

// This class helps us to validate the generated jwt token 
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserInfoService userDetailsService;
	private final ObjectMapper objectMapper;

	public JwtAuthFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		val requser = request.getParameter("username");
		val reqpass = request.getParameter("password");
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			try {
				username = jwtService.extractUsername(token);
			} catch (Exception e) {
				HashMap<String, Object> data = new HashMap<>();
				data.put("status", "-1");
				data.put("error", e.getMessage());
				response.setHeader("content-type", "application/json");
				response.setStatus(response.SC_UNAUTHORIZED);
				response.getWriter().write(objectMapper.writeValueAsString(data));
				return;
			}

		} else if (requser != null && reqpass != null) {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(requser, reqpass));
			if (authentication.isAuthenticated()) {
				token = jwtService.generateToken(requser);
				username = requser;
				Cookie cookie = new Cookie("jwt", token);
				response.addCookie(cookie);
				String userAgent = request.getHeader("User-Agent");
				// check the user agent string to determine if the request is from a native app
				System.out.println("PranvaaEET userAgent is " + userAgent);
				if (userAgent != null && userAgent.contains("Native")) {
					// do something if the request is from a native app
				} else {
				}
			}
		} else {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("jwt")) {
						token = cookie.getValue();
						try {
							username = jwtService.extractUsername(token);
						} catch (Exception e) {
							token = null;
							username = null;
							Cookie deleteServletCookie = new Cookie("jwt", null);
							deleteServletCookie.setMaxAge(0);
							response.addCookie(deleteServletCookie);
						}
						break;
					}
				}
			}
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}
// public int IsMobileDevice(HttpServletRequest request){
//                     String userAgetnt = request.getHeader("User-Agent");
//                     String deviceName = "Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini";
//                     return deviceName.indexOf(userAgetnt);
//                 }
}
