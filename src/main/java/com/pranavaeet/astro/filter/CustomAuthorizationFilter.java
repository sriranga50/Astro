package com.pranavaeet.astro.filter;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// import com.google.api.services.storage.Storage.BucketAccessControls.List;
import com.pranavaeet.astro.service.ControllerAccessService;

import java.io.IOException;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private ControllerAccessService controllerAccessService; 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get the authentication details from the SecurityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Custom authorization logic for specific endpoints
            if (isAuthorizedEndpoint(requestURI, httpMethod, userDetails)) {
                // Proceed with the request
                filterChain.doFilter(request, response);
            } else {
                // Deny access and redirect to access denied page
                response.sendRedirect("/access-denied");
            }
        } else {
            // Proceed with the request if no authentication is present
            filterChain.doFilter(request, response);
        }
    }

    private boolean isAuthorizedEndpoint(String requestURI,  String httpMethod, UserDetails userDetails) {
        // Example: Allow only users with ROLE_ADMIN to access /admin/** endpoints

        List<String> controllerlist=controllerAccessService.findAllnames();
        for(String controller:controllerlist){
            List<String> allowedRoles = controllerAccessService.getAllowedRoles(controller);
        if (requestURI.startsWith(controller)) {
            System.out.println(userDetails.getUsername());
            return userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority ->  allowedRoles.contains(grantedAuthority.getAuthority()));
        }
    }
        // Add more endpoint-specific authorization logic here
        return true;
    }
}
