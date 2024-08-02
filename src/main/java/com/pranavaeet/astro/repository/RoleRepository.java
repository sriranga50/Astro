package com.pranavaeet.astro.repository;

import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.entity.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String Name);
    Optional<Role> findById(Long id);
    @Valid Role save(Role role);
    List<Role> findAll();
    Page<Role> findAll(Pageable pageable);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_role WHERE role_id = :roleId", nativeQuery = true)
    void deleteByRoleId(@Param("roleId") Long roleId);
    @Query("SELECT n.name FROM Role n")
    List<String> fingAllNames();
}
