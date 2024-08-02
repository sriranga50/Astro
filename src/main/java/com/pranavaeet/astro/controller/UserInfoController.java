package com.pranavaeet.astro.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.repository.RoleRepository;
import com.pranavaeet.astro.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pranavaeet.astro.entity.SearchParameters;
import  com.pranavaeet.astro.entity.UserInfo;
import  com.pranavaeet.astro.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserInfoController {
	
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	private RoleService roleService;

    @ModelAttribute("params")
	public SearchParameters params() {
		return new SearchParameters();

	}

	@RequestMapping("/list")
	public String users(Model model, @ModelAttribute("params") SearchParameters params, HttpServletRequest request) {

		request.getSession().setAttribute("params", params);
		return findPaginated(params.getPage(), params.getSize(), params.getKeyword(), model, request);
	}

	@RequestMapping("/list/page/{pageNo}")
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

	@RequestMapping("/list/page/{pageNo}/{size}/{keyword}")
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
		Page<UserInfo> page = userInfoService.findPaginated(params.getKeyword(), params.getSize(), params.getPage());
		List<UserInfo> userInfos = page.getContent();
		List<Role> allroles=roleService.findAll();
		model.addAttribute("users", userInfos);
		model.addAttribute("roles", allroles);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.	getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("params", params);
		request.getSession().setAttribute("params", params);
		return "user";
	}
	// @PreAuthorize("@customSecurityExpression.hasAnyRoleForController(authentication,'/user/add')")
	@RequestMapping("add")
	public String addUser(Model model) {
		model.addAttribute("user", new UserInfo());
		model.addAttribute("roles", roleService.findAll());
		model.addAttribute("existingUsernames", userInfoService.findAllUsernames());
		System.out.println(userInfoService.findAllUsernames());
		return "adduser";
	}

	@PostMapping("addsubmit")
	public String addSubmitUser(@Valid UserInfo user, @RequestParam("roleIds") List<Long> roleIds,RedirectAttributes redirectAttributes, BindingResult result) {
		if (result.hasErrors()) {
			return "adduser";
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		user.setCreatedby(authentication.getName());
		if(!roleIds.isEmpty()) {
			user.setRoles(roleService.findAllById(roleIds));
		}
		user = userInfoService.addUser(user);
		redirectAttributes.addFlashAttribute("message", "Successfully Added the User");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:list";
	}

	// @PreAuthorize("@customSecurityExpression.hasAnyRoleForController(authentication,'/user/edit/{id}')")
	@GetMapping("edit/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		UserInfo user = userInfoService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
		model.addAttribute("user", user);
		model.addAttribute("roles", roleService.findAll());
		model.addAttribute("existingUsernames", userInfoService.findAllUsernames());
		System.out.println("PranavaEET - The username is " + System.getProperty("user.name"));
		return "updateuser";
	}

	@PostMapping("update/{id}")
	public String updateSubject(@PathVariable("id") long id, @RequestParam("roleIds") List<Long> roleIds, RedirectAttributes redirectAttributes,
								@Valid UserInfo user, BindingResult result,
								Model model) {
		if (result.hasErrors()) {
			user.setId(id);
			return "updateuser";
		}
		UserInfo userExisting = userInfoService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
		user.setPassword(userExisting.getPassword());
		user.setCreatedby(userExisting.getCreatedby());
		if(!roleIds.isEmpty()) {
			user.setRoles(roleService.findAllById(roleIds));
		}
		else{
			user.setRoles(userExisting.getRoles());
		}
		userInfoService.save(user);
		redirectAttributes.addFlashAttribute("message", "Successfully Updated the User");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/user/list";
	}

	@GetMapping("delete/{id:\\d+}")
	public String deleteUserInfo(@PathVariable("id") long id, RedirectAttributes redirectAttributes, Model model) {
		try {
			userInfoService.deleteById(id);
			redirectAttributes.addFlashAttribute("message", "Successfully Deleted the User");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message",
					"Sorry the User could not be deleted error - " + e.getMessage());
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		}
		return "redirect:/user/list";
	}

}
