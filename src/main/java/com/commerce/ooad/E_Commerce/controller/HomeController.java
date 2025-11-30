package com.commerce.ooad.E_Commerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.CartItemSQL;
import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import com.commerce.ooad.E_Commerce.repository.UserRepository;
import com.commerce.ooad.E_Commerce.repository.ProductRepository;
import com.commerce.ooad.E_Commerce.repository.CartRepository;
import com.commerce.ooad.E_Commerce.repository.CartItemRepository;
import com.commerce.ooad.E_Commerce.repository.PaymentMethodRepository;

@Controller
public class HomeController {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentMethodRepository paymentMethodRepository;

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

    @GetMapping("/2")
    public String page2() {
        return "page2";
    }

    public HomeController(UserRepository userRepository, ProductRepository productRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserSQL());
        return "registration-form";
    }

    @PostMapping("/register")
    public String registerUser(UserSQL user, Model model) {
        if (user.getState() == null || user.getState().isEmpty()) {
            model.addAttribute("stateError", "Please select your state.");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("passwordError", "Password cannot be empty.");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            model.addAttribute("emailError", "Email cannot be empty.");
        } else if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already exists. Please choose another.");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            model.addAttribute("usernameError", "Username cannot be empty.");
        } else if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("usernameError", "Username already exists. Please choose another.");
        }

        if (model.containsAttribute("stateError") ||
                model.containsAttribute("passwordError") ||
                model.containsAttribute("emailError") ||
                model.containsAttribute("usernameError")) {

            model.addAttribute("user", user);
            return "registration-form";
        }

        userRepository.save(user);
        return "redirect:/registration-success";
    }

    @GetMapping("/registration-success")
    public String registrationSuccess() {
        return "registration-success";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");

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
                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("loginError", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);
        return "dashboard";
    }

    @GetMapping("/shop")
    public String viewShop(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        List<ProductSQL> allProducts = productRepository.findAll();

        model.addAttribute("products", allProducts);

        return "shop";
    }

    @GetMapping("/payment-methods")
    public String viewPaymentMethods(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        UserSQL currentUser = (UserSQL) session.getAttribute("user");
        List<PaymentMethodSQL> paymentMethods = paymentMethodRepository.findByUser(currentUser);

        model.addAttribute("paymentMethods", paymentMethods);

        return "payment-methods";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

