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
import paymentStrategy.PaymentStrategy;

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

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        UserSQL currentUser = (UserSQL) session.getAttribute("user");

        Optional<CartSQL> optionalCart = cartRepository.findByUser(currentUser);

        CartSQL cart = optionalCart.orElseGet(() -> {
            return new CartSQL(currentUser);
        });

        model.addAttribute("cart", cart);
        return "cart";
    }
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam Integer quantity,
                           HttpSession session,
                           Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        UserSQL currentUser = (UserSQL) session.getAttribute("user");
        Optional<CartSQL> userCart = cartRepository.findByUser(currentUser);
        CartSQL cart; 
        if(userCart.isPresent()){
            cart = userCart.get();
        }
        else {
                CartSQL newCart = new CartSQL(currentUser);
                cart = cartRepository.save(newCart);
        }
        Optional<ProductSQL> currProduct = productRepository.findById(productId);
        if (!currProduct.isPresent()) {
            model.addAttribute("error", "Product does not exist");
            return "redirect:/shop";
        }
        ProductSQL product = currProduct.get();
        Optional<CartItemSQL> itemInCart = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if(itemInCart.isPresent()) {
            CartItemSQL existingItem = itemInCart.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        }
        else{
            CartItemSQL newItem = new CartItemSQL(cart, product, quantity);
            cartItemRepository.save(newItem);
        }

        return "redirect:/shop";
    }

    @GetMapping("/payment/add")
    public String showAddPaymentMethodForm(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        return "add-payment-method";
    }

    @PostMapping("/payment/add")
    public String addPaymentMethod(@RequestParam String paymentType,
                                @RequestParam BigDecimal balance,
                                @RequestParam(required = false) String paymentEmail,
                                @RequestParam(required = false) String paymentPassword,
                                @RequestParam(required = false) String paymentCardNumber,
                                @RequestParam(required = false) String paymentCardPin,
                                @RequestParam(required = false) String paymentCardName,
                                HttpSession session,
                                Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        UserSQL currentUser = (UserSQL) session.getAttribute("user");

        PaymentMethodSQL newPaymentMethod = new PaymentMethodSQL(
            currentUser,
            paymentType,
            balance,
            paymentEmail,
            paymentPassword,
            paymentCardNumber,
            paymentCardPin,
            paymentCardName
        );

        try {
            PaymentStrategy strategy = newPaymentMethod.toPaymentStrategy();
            if (!strategy.validate()) {
                model.addAttribute("error", "Invalid payment details. Please check your information again");
                return "add-payment-method";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "add-payment-method";
        }

        paymentMethodRepository.save(newPaymentMethod);

        return "redirect:/payment-methods";
    }
    @PostMapping("/payment/delete")
    public String deletePaymentMethod(@RequestParam Long methodId, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        UserSQL currentUser = (UserSQL) session.getAttribute("user");

        Optional<PaymentMethodSQL> paymentMethodOptional = paymentMethodRepository.findById(methodId);
        
        if (paymentMethodOptional.isPresent()) {
            PaymentMethodSQL paymentMethod = paymentMethodOptional.get();

            if (paymentMethod.getUser().getId().equals(currentUser.getId())) {
                paymentMethodRepository.delete(paymentMethod);
            }
        }

        return "redirect:/payment-methods";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

