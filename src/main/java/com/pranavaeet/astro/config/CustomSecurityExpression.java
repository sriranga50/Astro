package com.pranavaeet.astro.config;

import com.google.cloud.Role;
import com.pranavaeet.astro.service.ControllerAccessService;
import com.pranavaeet.astro.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomSecurityExpression {
    @Autowired
    private ControllerAccessService controllerAccessService; 
    public boolean hasAnyRole(Authentication authentication, List<String> roles) {
        if (authentication == null || roles == null || roles.isEmpty()) {
            return false;
        }
        for (String role : roles) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyRoleForController(Authentication authentication,String controller) {
        List<String> allowedRoles = controllerAccessService.getAllowedRoles(controller);
        return hasAnyRole(authentication, allowedRoles);
    }
}
