package com.pranavaeet.astro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pranavaeet.astro.entity.InputParameters;
import com.pranavaeet.astro.service.CalculationService;
import com.pranavaeet.astro.service.OutputStructure;

@Controller
@RequestMapping("/calculatePosition")
public class PositionCalculatorController {
    @Autowired
    CalculationService calculationService;

    @RequestMapping("parameterInput")
    public String displayInputForm(Model model) {
        model.addAttribute("inputparameters", new InputParameters());
        return "planetcalculator";
    }

    @PostMapping("calculate")
    public String positionCalculation(@ModelAttribute InputParameters inputParameters, Model model) {

        List<OutputStructure> planetslocations = calculationService.LocationCalculator(inputParameters.getLat(),
                inputParameters.getLon(), inputParameters.getDatetime());
        model.addAttribute("planetslocations", planetslocations);
        System.out.println("lat" + inputParameters.getLat());
        System.out.println("lon" + inputParameters.getLon());
        System.out.println("datetime" + inputParameters.getDatetime());
        for (OutputStructure outputStructure : planetslocations) {
            System.out.println(outputStructure.planetname);
            System.out.println(outputStructure.r);
            System.out.println(outputStructure.phi);
            System.out.println(outputStructure.elevation);
            System.out.println(outputStructure.azimuth);
        }
        return "planetpositions";
    }

    @PostMapping("calculate2")
    public ResponseEntity<Object> positionCalculation2(@RequestBody InputParameters inputParameters) {

        List<OutputStructure> planetslocations = calculationService.LocationCalculator(inputParameters.getLat(),
                inputParameters.getLon(), inputParameters.getDatetime());
        System.out.println("lat" + inputParameters.getLat());
        System.out.println("lon" + inputParameters.getLon());
        System.out.println("datetime" + inputParameters.getDatetime());
        // for(OutputStructure outputStructure:planetslocations){
        // System.out.println(outputStructure.planetname);
        // System.out.println(outputStructure.r);
        // System.out.println(outputStructure.phi);
        // System.out.println(outputStructure.elevation);
        // System.out.println(outputStructure.azimuth);
        // }
        return new ResponseEntity<>(planetslocations, HttpStatus.OK);
    }

}

// @RequestParam("lat") double lat, @RequestParam("lon") double lon,
// @RequestParam("datetime") String datetime,
