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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Optional;
import com.pranavaeet.astro.entity.ControllerAccess;
import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.entity.SearchParameters;
import com.pranavaeet.astro.entity.UserInfo;
import com.pranavaeet.astro.service.ControllerAccessService;
import com.pranavaeet.astro.service.RoleService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/controller")
public class MyController {
    @Autowired
    ControllerAccessService controllerAccessService;
    @Autowired
	private RoleService roleService;
    @ModelAttribute("params")
	public SearchParameters params() {
		return new SearchParameters();

	}
    @RequestMapping("/controllerlist")
	public String controller(Model model, @ModelAttribute("params") SearchParameters params, HttpServletRequest request) {

		request.getSession().setAttribute("params", params);
		return findPaginated(params.getPage(), params.getSize(), params.getKeyword(), model, request);
	}
    @GetMapping("/controllerdisplay")
    public String display(Model model){
        return "controllers";
    } 

    @RequestMapping("/controllerlist/page/{pageNo}")
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

    @RequestMapping("/controllerlist/page/{pageNo}/{size}/{keyword}")
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
		Page<ControllerAccess> page = controllerAccessService.findPaginated(params.getKeyword(), params.getSize(), params.getPage());
		List<ControllerAccess> controllers = page.getContent();
		List<Role> allroles=roleService.findAll();
		model.addAttribute("controllers", controllers);
		model.addAttribute("roles", allroles);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("params", params);
		request.getSession().setAttribute("params", params);
		return "controlleraccess";
	}
    @RequestMapping("addcontroller")
	public String addUser(Model model) {
		model.addAttribute("controllers", new ControllerAccess());
		model.addAttribute("roles", roleService.findAll());
		model.addAttribute("existingcontrollers", controllerAccessService.findAllnames());
		return "addnewcontroller";
	}

    @PostMapping("addcontrollersubmit")
	public String addSubmitUser(@Valid ControllerAccess controller, @RequestParam("roleIds") List<Long> roleIds,RedirectAttributes redirectAttributes, BindingResult result) {
		if (result.hasErrors()) {
			return "addnewcontroller";
		}
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!roleIds.isEmpty()) {
			controller.setRoles(roleService.findAllById(roleIds));
		}
		controller = controllerAccessService.addUser(controller);
		redirectAttributes.addFlashAttribute("message", "Successfully Added the User");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:controllerlist";
	}
    
    @GetMapping("editcontroller/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		ControllerAccess controller = controllerAccessService.findById(id);
		model.addAttribute("controller", controller);
		model.addAttribute("allroles", roleService.findAll());
		model.addAttribute("existingcontrollers", controllerAccessService.findAllnames());
		System.out.println("PranavaEET - The username is " + System.getProperty("controller.name"));
		return "updatecontroller";
	}

	@PostMapping("updatecontroller/{id}")
	public String updateSubject(@PathVariable("id") long id, @RequestParam("roleIds") List<Long> roleIds, RedirectAttributes redirectAttributes,
								@Valid ControllerAccess controller, BindingResult result,
								Model model) {
		if (result.hasErrors()) {
			controller.setId(id);
			return "updatecontroller";
		}
		ControllerAccess controllerExisting = controllerAccessService.findById(id);
		if(!roleIds.isEmpty()) {
			controller.setRoles(roleService.findAllById(roleIds));
		}
		else{
			controller.setRoles(controllerExisting.getRoles());
		}
		controllerAccessService.save(controller);
		redirectAttributes.addFlashAttribute("message", "Successfully Updated the Controller");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/controller/controllerlist";
	}

    @GetMapping("deletecontroller/{id:\\d+}")
	public String deleteUserInfo(@PathVariable("id") long id, RedirectAttributes redirectAttributes, Model model) {
		try {
			controllerAccessService.deleteById(id);
			redirectAttributes.addFlashAttribute("message", "Successfully Deleted the COntroller");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message",
					"Sorry the Controller could not be deleted error - " + e.getMessage());
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		}
		return "redirect:/controller/controllerlist";
	}
}
