package com.pranavaeet.astro.config;

import java.io.IOException;
import com.pranavaeet.astro.filter.CustomAuthorizationFilter;
import com.pranavaeet.astro.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.pranavaeet.astro.service.UserInfoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; 

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig { 

	@Autowired
	private JwtAuthFilter authFilter; 
	@Autowired
	private UserInfoService userInfoservice; 
	@Autowired
    private CustomAuthorizationFilter customAuthorizationFilter;
@Bean
MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
    return new MvcRequestMatcher.Builder(introspector);
}
	// Configuring HttpSecurity 
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception { 
		http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(mvc.pattern("/privacypolicy"), mvc.pattern("/support"), mvc.pattern("/feedback"), mvc.pattern("/addcontactform"), mvc.pattern("/testnotify"),mvc.pattern("/favicon.ico"),mvc.pattern("/guest/*"),mvc.pattern("/guest/*/*"),mvc.pattern("/guest/*/*/*"),mvc.pattern("/auth/generatetoken"),mvc.pattern("/auth/addnewuser"),mvc.pattern("/auth/confirmotp"),mvc.pattern("/auth/signin"),mvc.pattern("/auth/confirmloginotp"),mvc.pattern("/auth/processsignup")).permitAll()
            .anyRequest().authenticated()
        ).formLogin(form -> form
        	.successForwardUrl("/auth/welcome")    
			.failureUrl("/error?error=true")	
			.successHandler(new SavedRequestAwareAuthenticationSuccessHandler() {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // run custom logics upon successful login
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("The user " + username + " has logged in.");        
        super.onAuthenticationSuccess(request, response, authentication);
    }                      
})	
        );
		
		http.sessionManagement(management -> management
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Register custom filter
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/access-denied");
                        }));
       return  http.build();
	} 

	// Password Encoding 
	@Bean
	public PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder(); 
	} 

	@Bean
	public AuthenticationProvider authenticationProvider() { 
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); 
		authenticationProvider.setUserDetailsService(userInfoservice); 
		authenticationProvider.setPasswordEncoder(passwordEncoder()); 
		return authenticationProvider; 
	} 

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { 
		return config.getAuthenticationManager(); 
	} 


} 
