package edu.co.icesi.proyectofinal.controller;


import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(Model model){
        return "auth/login";
    }

    @GetMapping("/logout-confirm")
    public String logoutConfirm() {
        return "auth/logout";
    }

    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "auth/logout-success";
    }

    @GetMapping("/home")
    public String showHome(Model model, @AuthenticationPrincipal UserDetails userDetails){

        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "home";}

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("newUser", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("newUser") User user,BindingResult result){

        if(result.hasErrors()){
            if (result.hasFieldErrors("institutionalEmail")) {
                return "redirect:/register?domain";
            }

            return "auth/register";
        }

        userService.saveUser(user);

        return "redirect:/login?success";
    }

}