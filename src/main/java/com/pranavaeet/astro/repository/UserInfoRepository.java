package com.pranavaeet.astro.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pranavaeet.astro.entity.UserInfo;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid; 

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> { 
	List<UserInfo> findAll();
	@Valid UserInfo save(UserInfo userInfo);
	Optional<UserInfo> findByUsername(String username);
	Optional<UserInfo> findById(long id);
    Page<UserInfo> findAll(Pageable pageable);

	@Query("SELECT u.username FROM UserInfo u")
	List<String> findAllUsernames();
}
