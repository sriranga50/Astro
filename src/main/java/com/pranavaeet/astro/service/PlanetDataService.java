package com.pranavaeet.astro.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pranavaeet.astro.entity.PlanetData;
import com.pranavaeet.astro.repository.PlanetDataRepository;

@Service
public class PlanetDataService {

    @Autowired
    private PlanetDataRepository planetDataRepository;

    public PlanetData addPlanet(PlanetData planetData ) {
        planetDataRepository.save(planetData);
        return planetData;
    }

    public Optional<PlanetData> findById(long id) {
        return planetDataRepository.findById(id);
    }

    public @Valid PlanetData save(@Valid PlanetData planetData) {

        return planetDataRepository.save(planetData);
    }

//    private String planetname;
//    private double meanlongitude;
//    private double semimajoraxis;
//    private double eccentricity;
//    private double inclination;
//    private double longitudeofacendingnode;
//    private double logitudeofperihelion;
//    private double meananomaly;
//    private double orbitalperiod;

    public Page<PlanetData> findPaginated(String keyword, int size, int page) {
        Pageable pageable = Pageable.unpaged();
        if (size != -1)
            pageable = PageRequest.of(page - 1, size);
        String key = keyword == null ? "" : keyword;
        if (key.isEmpty()) {
            return planetDataRepository.findAll(pageable);
        }
        return planetDataRepository.findAll(pageable);
    }

    public String deleteById(Long id) {
        PlanetData planetData  = planetDataRepository.findById(id).orElse(null);
        if (planetData != null) {
            planetDataRepository.deleteById(id);
            return "Successfully Deleted";
        }
        return "Role not found";
    }

    public List<PlanetData> findAll() {
        return planetDataRepository.findAll();
    }

    public List<String> findAllNames() {
        // TODO Auto-generated method stub
        return planetDataRepository.findAllNames();
    }
    // public boolean planetExists(String name) {
    //     // return planetDataRepository.findByPlanetname(name) != null;
    //     List<String> allPlanetNames=planetDataRepository.findAllNames();
    //     for(String planet:allPlanetNames){
    //         if (planet.equals(name)){
    //             return true;
    //         }
    //     }
    //     return false;
    // }


}
