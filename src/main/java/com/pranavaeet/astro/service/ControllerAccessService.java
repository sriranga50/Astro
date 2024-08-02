package com.pranavaeet.astro.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import com.google.common.base.Optional;
import com.pranavaeet.astro.entity.ControllerAccess;
import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.repository.ControllerAccessRepository;
@Service
public class ControllerAccessService {
    @Autowired
    ControllerAccessRepository controllerAccessRepository;
    @Autowired
    RoleService roleService;

    public ControllerAccess addUser(ControllerAccess controller) {
		Role defaultRole = roleService.findByName("ROLE_USER");
		if (defaultRole != null && 	controller.getRoles().isEmpty()) {
			controller.addRole(defaultRole);
		}
		controllerAccessRepository.save(controller);
		return controller;
	}
    public @Valid ControllerAccess save(@Valid ControllerAccess controller) {
		return controllerAccessRepository.save(controller);
	}

	public ControllerAccess findById(long id) {
		return controllerAccessRepository.findById(id);
	}

    public Page<ControllerAccess> findPaginated(String keyword, int size, int page) {
		Pageable pageable = Pageable.unpaged();
		if (size != -1)
			pageable = PageRequest.of(page - 1, size);
		String key = keyword == null ? "" : keyword;
		if (key.isEmpty()) {
			return controllerAccessRepository.findAll(pageable);
		}
		return controllerAccessRepository.findAll(pageable);
	}


    public String deleteById(long id) {
		ControllerAccess controller = controllerAccessRepository.findById(id);
		if (controller != null) {
			// Detach roles from user
			controller.getRoles().clear();
			controllerAccessRepository.deleteById(id);
		}
		return "Successfully Deleted";
	}

	public List<String> findAllnames() {
		return controllerAccessRepository.findAllnames();
	}
	public List<String> getAllowedRoles(String controller) {
		List<Role> roles=controllerAccessRepository.findRolesByControllerName(controller);
		List<String> rolenames=new ArrayList<>();
		for(Role role:roles){
			rolenames.add(role.getName());
		}
		return rolenames;
	}

}
