package com.pranavaeet.astro.service;

import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.entity.UserInfo;
import com.pranavaeet.astro.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role addRole(Role role ) {
        roleRepository.save(role);
        return role;
    }

    public Optional<Role> findById(long id) {
        return roleRepository.findById(id);
    }

    public Role findByName(String name){
        return roleRepository.findByName(name);
    }

    public @Valid Role save(@Valid Role role) {

        return roleRepository.save(role);
    }

    public Page<Role> findPaginated(String keyword, int size, int page) {
        Pageable pageable = Pageable.unpaged();
        if (size != -1)
            pageable = PageRequest.of(page - 1, size);
        String key = keyword == null ? "" : keyword;
        if (key.isEmpty()) {
            return roleRepository.findAll(pageable);
        }
        return roleRepository.findAll(pageable);
    }

    @Transactional
    public void deleteRolesByRoleId(Long roleId) {
        roleRepository.deleteByRoleId(roleId);
    }

    @Transactional
    public String deleteById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role != null) {
            roleRepository.deleteById(id);
            return "Successfully Deleted";
        }
        return "Role not found";
    }


    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public List<Role> findAllById(List<Long> roleIds) {
        return roleRepository.findAllById(roleIds);
    }

    public boolean roleExists(String name) {
        return roleRepository.findByName(name) != null;
    }

    public List<String> findAllNames() {
        return roleRepository.fingAllNames();
    }

}
