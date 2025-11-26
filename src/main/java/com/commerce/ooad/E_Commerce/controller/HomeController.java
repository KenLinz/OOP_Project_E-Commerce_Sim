package com.commerce.ooad.E_Commerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.UserRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class HomeController {
    private final UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }


    @GetMapping("/1")
    @ResponseBody
    public String page1() {
        return "This is Page 1";
    }

    // This method expects src/main/resources/templates/page2.html
    @GetMapping("/2")
    public String page2() {
        return "page2";
    }

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserSQL());
        return "registration-form";
    }

    @PostMapping("/register")
    public String registerUser(UserSQL user) {
        userRepository.save(user);
        return "redirect:/registration-success";
    }

    @GetMapping("/registration-success")
    public String registrationSuccess() {
        return "registration-success";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String currentUsername = (String) session.getAttribute("loggedInUser");

        if (currentUsername != null) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {

        Optional<UserSQL> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            UserSQL user = userOptional.get();

            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", user.getUsername());
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("loginError", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/login";
    }
}

