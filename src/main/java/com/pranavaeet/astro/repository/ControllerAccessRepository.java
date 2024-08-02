package com.pranavaeet.astro.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pranavaeet.astro.entity.Role;
import com.google.common.base.Optional;
import com.pranavaeet.astro.entity.ControllerAccess;



public interface ControllerAccessRepository extends JpaRepository<ControllerAccess,Long> {
    List<ControllerAccess> findAll();
    ControllerAccess findById(long id);
    Page<ControllerAccess> findAll(Pageable pageable);

    @Query("SELECT u.name FROM ControllerAccess u")
	List<String> findAllnames();
    
    @Query("SELECT r FROM Role r JOIN r.controllers c WHERE c.name = :name")
    List<Role> findRolesByControllerName(@Param("name") String name);
}
