package com.pranavaeet.astro.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pranavaeet.astro.entity.PlanetData;

@Repository
public interface PlanetDataRepository extends JpaRepository<PlanetData,Long> {
    Page<PlanetData> findAll(Pageable pageable);
    Optional<PlanetData> findById(Long id);
    @Valid
    PlanetData save(PlanetData planetData );
    List<PlanetData> findAll();
    @Query("SELECT n.planetname FROM PlanetData n")
    List<String> findAllNames();
    String findByPlanetname(String planetname);


//    List<String> findAllPlanetname();
}
