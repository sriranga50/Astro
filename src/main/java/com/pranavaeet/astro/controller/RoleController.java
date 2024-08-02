package com.pranavaeet.astro.controller;

import com.pranavaeet.astro.entity.Role;
import com.pranavaeet.astro.entity.SearchParameters;
import com.pranavaeet.astro.entity.UserInfo;
import com.pranavaeet.astro.repository.RoleRepository;
import com.pranavaeet.astro.service.RoleService;
import com.pranavaeet.astro.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    @ModelAttribute("params")
    public SearchParameters params() {
        return new SearchParameters();
    }

    @RequestMapping("/rolelist")
    public String users(Model model, @ModelAttribute("params") SearchParameters params, HttpServletRequest request) {

        request.getSession().setAttribute("params", params);
        return findPaginated(params.getPage(), params.getSize(), params.getKeyword(), model, request);
    }

    @RequestMapping("/rolelist/page/{pageNo}")
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

    @RequestMapping("/rolelist/page/{pageNo}/{size}/{keyword}")
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
        Page<Role> page = roleService.findPaginated(params.getKeyword(), params.getSize(), params.getPage());
        List<Role> allroles = page.getContent();
        model.addAttribute("roles", allroles);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.	getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("params", params);
        request.getSession().setAttribute("params", params);
        return "role";
    }

    @RequestMapping("addrole")
    public String addUser(Model model) {
        model.addAttribute("role", new Role());
        return "addnewrole";
    }

    @PostMapping("addrolesubmit")
    public String addSubmitUser(@Valid Role role, RedirectAttributes redirectAttributes, BindingResult result) {
        if (roleService.roleExists(role.getName())) {
            result.rejectValue("name", "error.role", "Role name already exists");
        }
        if (result.hasErrors()) {
            return "addnewrole";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        role.setCreatedby(authentication.getName());
        role = roleService.addRole(role);
        redirectAttributes.addFlashAttribute("message", "Successfully Added the Role");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/role/rolelist";
    }

    @GetMapping("editrole/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Role role = roleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("role", role);
        model.addAttribute("existingrolenames", roleService.findAllNames());
        System.out.println("PranavaEET - The username is " + System.getProperty("role.name"));
        return "updaterole";
    }

    @PostMapping("updaterole/{id}")
    public String updateSubject(@PathVariable("id") long id, RedirectAttributes redirectAttributes,
                                @Valid Role role, BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            role.setId(id);
            return "updaterole";
        }
        Role roleExisting = roleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));

        role.setCreatedby(roleExisting.getCreatedby());
        role.setUsers(roleExisting.getUsers());
        roleService.save(role);
        redirectAttributes.addFlashAttribute("message", "Successfully Updated the User");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/role/rolelist";
    }


    @GetMapping("deleterole/{id:\\d+}")
    public String deleteUserInfo(@PathVariable("id") long id, RedirectAttributes redirectAttributes, Model model) {
        try {
            roleService.deleteRolesByRoleId(id); // Delete associations in the join table first
            String message = roleService.deleteById(id); // Then delete the role itself
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Sorry the Role could not be deleted. Error - " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/role/rolelist";
    }
}
