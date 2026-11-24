package edu.co.icesi.proyectofinal.controller;

import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('LIST_USER')")
    public String userList(Model model){

        List<User> users = userService.getUsers();

        model.addAttribute("users", users);

        return "users/list";
    }

    @GetMapping("/assign-role/{idUser}")
    @PreAuthorize("hasAnyAuthority('ASSIGN_ROLE')")
    public String assignRoleForm(@PathVariable Integer idUser, Model model){

        User user = userService.getUserById(idUser);

        List<Long> currentRoleIds = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getId())
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("roles", userService.getAllRoles());
        model.addAttribute("currentRoleIds", currentRoleIds);

        return "users/assign-role";
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasAnyAuthority('ASSIGN_ROLE')")
    public String assignRole(@RequestParam Integer userId,
                             @RequestParam(required = false) List<Long> roles){

        userService.updateUserRoles(userId, roles);

        return "redirect:/users?success";
    }

    @GetMapping("/edit/{idUser}")
    @PreAuthorize("hasAnyAuthority('EDIT_USER')")
    public String editUserForm(@PathVariable Integer idUser, Model model){
        User user = userService.getUserById(idUser);
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/edit/{idUser}")
    @PreAuthorize("hasAnyAuthority('EDIT_USER')")
    public String editUser(@PathVariable Integer idUser, @ModelAttribute("user") User user){
        userService.updateUser(idUser, user);
        return "redirect:/users/list?success";
    }


    //Cambiar Principal por @AuthenticationPrincipal CustomUserDetails userDetails al integrar ya el modulo de Seguridad Completo.
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('VIEW_OWN_PROFILE')")
    public String viewProfile(Model model, Principal principal){

        User user = userService.getUserByEmail(principal.getName());

        model.addAttribute("user", user);

        return "users/profile";
    }

    @GetMapping("/edit-profile")
    @PreAuthorize("hasAnyAuthority('EDIT_OWN_PROFILE')")
    public String editProfile(Model model, Principal principal){

        User user = userService.getUserByEmail(principal.getName());

        model.addAttribute("user", user);

        return "users/edit-profile";
    }

    @PostMapping("/edit-profile")
    @PreAuthorize("hasAnyAuthority('EDIT_OWN_PROFILE')")
    public String updateProfile(@ModelAttribute User user, Principal principal){

        userService.updateUserProfile(principal.getName(), user);

        return "redirect:/users/profile?success";
    }

}