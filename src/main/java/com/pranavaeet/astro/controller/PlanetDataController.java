package com.pranavaeet.astro.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pranavaeet.astro.entity.PlanetData;
import com.pranavaeet.astro.entity.SearchParameters;
import com.pranavaeet.astro.repository.PlanetDataRepository;
import com.pranavaeet.astro.service.PlanetDataService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/planet")
public class PlanetDataController {

    @Autowired
    PlanetDataService planetDataService;
    @Autowired
    private PlanetDataRepository planetDataRepository;

    @ModelAttribute("params")
    public SearchParameters params() {
        return new SearchParameters();
    }

    @RequestMapping("/planetlist")
    public String users(Model model, @ModelAttribute("params") SearchParameters params, HttpServletRequest request) {

        request.getSession().setAttribute("params", params);
        return findPaginated(params.getPage(), params.getSize(), params.getKeyword(), model, request);
    }

    @RequestMapping("/planetlist/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @ModelAttribute("params") SearchParameters params, Model model, HttpServletRequest request) {
        if (params == null) {
            params = (SearchParameters) request.getSession().getAttribute("params");
        }
        if (params == null)
            params = new SearchParameters();
        request.getSession().setAttribute("params", params);
        return findPaginated(pageNo, params.getSize(), params.getKeyword(), model, request);
    }

    @RequestMapping("/planetlist/page/{pageNo}/{size}/{keyword}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @PathVariable(value = "size", required = false) int pageSize,
                                @PathVariable(value = "keyword", required = false) String keyword, Model model,
                                HttpServletRequest request) {
        SearchParameters params = null;
        if (request.getSession().getAttribute("params") != null) {
            params = (SearchParameters) request.getSession().getAttribute("params");
            params.setPage(pageNo);
            params.setSize(pageSize);
        } else {
            params = new SearchParameters();
            params.setPage(pageNo);
            params.setSize(10);
        }
        Page<PlanetData> page = planetDataService.findPaginated(params.getKeyword(), params.getSize(), params.getPage());
        List<PlanetData> allplanets = page.getContent();
        model.addAttribute("planets", allplanets);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.	getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("params", params);
        request.getSession().setAttribute("params", params);
        return "planetdata";
    }

    @RequestMapping("addplanet")
   public String addplanet(Model model) {
        model.addAttribute("planetdata", new PlanetData());
        model.addAttribute("existingplanetnames", planetDataService.findAllNames());

        return "addplanet";
    }

    @PostMapping("/addplanetsubmit")
    public String addPlanetSubmit(@Valid PlanetData planetData, RedirectAttributes redirectAttributes, BindingResult result) {
    //    if (planetDataService.planetExists(planetData.getPlanetname())) {
    //        result.rejectValue("planetname", "error.planetdata", "Planet name already exists");
    //    }

        if (result.hasErrors()) {
            return "addplanet";
        }
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        role.setCreatedby(authentication.getName());
        planetData = planetDataService.addPlanet(planetData);
        redirectAttributes.addFlashAttribute("message", "Successfully Added the Planet");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/planet/planetlist";
    }

    @GetMapping("editplanet/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        PlanetData planetData  = planetDataService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("planetdata", planetData);
        model.addAttribute("existingplanetnames", planetDataService.findAllNames());
        System.out.println("PranavaEET - The username is " + System.getProperty("planet.planetname"));
        return "updateplanet";
    }

    @PostMapping("updateplanet/{id}")
    public String updateSubject(@PathVariable("id") long id, RedirectAttributes redirectAttributes,
                                @Valid PlanetData planetData , BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            planetData.setId(id);
            return "updateplanet";
        }
        PlanetData planetDataExisting = planetDataService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));

       

        planetDataService.save(planetData);
        redirectAttributes.addFlashAttribute("message", "Successfully Updated the Planet");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/planet/planetlist";
    }


     @GetMapping("deleteplanet/{id:\\d+}")
    public String deletePlanetInfo(@PathVariable("id") long id, RedirectAttributes redirectAttributes, Model model) {
        try {
            planetDataService.deleteById(id); // Delete associations in the join table first
            String message = planetDataService.deleteById(id); // Then delete the role itself
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Sorry the Planet could not be deleted. Error - " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/planet/planetlist";
    }


}
