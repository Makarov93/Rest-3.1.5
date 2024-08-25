package makarov.springsecurity.controller;

import makarov.springsecurity.model.User;
import makarov.springsecurity.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getLoginPage() {
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "/admin";
    }

    @GetMapping(value = "/user")
    public String getUserPage(Principal principal, ModelMap model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute(user);
        return "/user";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}


