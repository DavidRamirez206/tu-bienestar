package edu.co.icesi.proyectofinal.controller;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.RoleService;
import edu.co.icesi.proyectofinal.services.PolicyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;
    private final PolicyService policyService;

    public RoleController(RoleService roleService, PolicyService policyService) {
        this.roleService = roleService;
        this.policyService = policyService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('LIST_ROLE')")
    public String listRoles(Model model){
        model.addAttribute("roles", roleService.getRoles());
        return "roles/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLE')")
    public String createRoleForm(Model model){
        model.addAttribute("newRole", new Role());
        return "roles/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLE')")
    public String createRole(@ModelAttribute("role") Role role){
        roleService.saveRole(role);
        return "redirect:/roles?success";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('DELETE_ROLE')")
    public String deleteRole(@PathVariable Long id){
        roleService.deleteRoleById(id);
        return "redirect:/roles?success";
    }

    @GetMapping("/assign-policies/{id}")
    @PreAuthorize("hasAnyAuthority('ASSIGN_POLICY')")
    public String assignPolicies(@PathVariable Long id, Model model){
        Role role = roleService.getRoleById(id);
        // Extraemos los Ids de las pólizas que ya tiene este rol
        List<Long> currentPolicyIds = role.getRolePolicies().stream()
                .map(rp -> rp.getPolicy().getId())
                .collect(Collectors.toList());
        model.addAttribute("currentPolicyIds", currentPolicyIds);
        model.addAttribute("role", roleService.getRoleById(id));
        model.addAttribute("policy", policyService.getPolicies());
        return "roles/assign-policies";
    }

    @PostMapping("/assign-policies")
    @PreAuthorize("hasAnyAuthority('ASSIGN_POLICY')")
    public String savePolicies(@RequestParam Long roleId,
                               @RequestParam(required = false) List<Long> policies){

        roleService.assignPolicies(roleId, policies);
        return "redirect:/roles?success";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_ROLE')")
    public String editRoleForm(@PathVariable Long id, Model model) {
        Role role = roleService.getRoleById(id);
        List<Policy> policies = policyService.getPolicies();

        List<Long> currentPolicyIds = role.getRolePolicies().stream()
                .map(rp -> rp.getPolicy().getId())
                .collect(Collectors.toList());
        model.addAttribute("policies", policies);
        model.addAttribute("role", role);
        model.addAttribute("currentPolicyIds", currentPolicyIds);
        return "roles/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_ROLE')")
    public String editRole(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam(required = false) List<Long> policies) {

        roleService.updateRole(id, name, policies);

        return "redirect:/roles?success";
    }
}