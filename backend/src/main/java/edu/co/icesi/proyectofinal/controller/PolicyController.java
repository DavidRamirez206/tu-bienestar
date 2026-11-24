package edu.co.icesi.proyectofinal.controller;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.services.PolicyService;
import edu.co.icesi.proyectofinal.services.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/policies")
public class PolicyController {

    private final PolicyService policyService;
    private final RoleService roleService;

    public PolicyController(PolicyService policyService, RoleService roleService) {
        this.policyService = policyService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('LIST_POLICY')")
    public String listPolicies(Model model){
        model.addAttribute("policies", policyService.getPolicies());
        return "policies/list";
    }

    @GetMapping("/assign-policies/{id}")
    @PreAuthorize("hasAnyAuthority('ASSIGN_POLICY')")
    public String assignPolicies(@PathVariable Long id, Model model){

        model.addAttribute("role", roleService.getRoleById(id));
        model.addAttribute("policies", policyService.getPolicies());

        return "roles/assign-policies";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyAuthority('CREATE_POLICY')")
    public String createPolicyForm(Model model){
        model.addAttribute("newPolicy", new Policy());
        return "policies/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('CREATE_POLICY')")
    public String createPolicy(@ModelAttribute("policy") Policy policy){
        policyService.savePolicy(policy);
        return "redirect:/policies?success";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_POLICY')")
    public String editPolicyForm(@PathVariable Long id, Model model) {
        Policy policy = policyService.getPolicyById(id);
        model.addAttribute("policy", policy);
        return "policies/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_POLICY')")
    public String editPolicy(@PathVariable Long id, @ModelAttribute("policy") Policy policy) {
        policyService.updatePolicy(id, policy);
        return "redirect:/policies?success";
    }


}