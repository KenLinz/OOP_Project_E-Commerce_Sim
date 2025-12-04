package com.commerce.ooad.E_Commerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import checkout.CheckoutResult;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.model.ProductSQL;
import com.commerce.ooad.E_Commerce.model.CartSQL;
import com.commerce.ooad.E_Commerce.model.PaymentMethodSQL;
import com.commerce.ooad.E_Commerce.service.AuthenticationService;
import com.commerce.ooad.E_Commerce.service.CartService;
import com.commerce.ooad.E_Commerce.service.ProductService;
import com.commerce.ooad.E_Commerce.service.PaymentMethodService;
import com.commerce.ooad.E_Commerce.service.CheckoutService;

@Controller
public class HomeController {

    private final AuthenticationService authenticationService;
    private final CartService cartService;
    private final ProductService productService;
    private final PaymentMethodService paymentMethodService;
    private final CheckoutService checkoutService;

    public HomeController(AuthenticationService authenticationService, CartService cartService, ProductService productService, PaymentMethodService paymentMethodService, CheckoutService checkoutService) {
        this.authenticationService = authenticationService;
        this.cartService = cartService;
        this.productService = productService;
        this.paymentMethodService = paymentMethodService;
        this.checkoutService = checkoutService;
    }

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

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserSQL());
        return "registration-form";
    }

    @PostMapping("/register")
    public String registerUser(UserSQL user, Model model) {
        try {
            authenticationService.register(user);
            return "redirect:/registration-success";
        } catch (AuthenticationService.RegistrationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "registration-form";
        }
    }

    @GetMapping("/registration-success")
    public String registrationSuccess() {
        return "registration-success";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (getCurrentUser(session).isPresent()) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               Model model,
                               HttpSession session) {
        Optional<UserSQL> userOptional = authenticationService.authenticate(username, password);

        if (userOptional.isPresent()) {
            UserSQL user = userOptional.get();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            return "redirect:/dashboard";
        }

        model.addAttribute("loginError", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Optional<UserSQL> user = getCurrentUser(session);
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("username", user.get().getUsername());
        return "dashboard";
    }

    @GetMapping("/shop")
    public String viewShop(HttpSession session, Model model) {
        if (getCurrentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        List<ProductSQL> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        return "shop";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam Integer quantity,
                            @RequestParam(required = false, defaultValue = "false") Boolean hasWarranty,
                            @RequestParam(required = false, defaultValue = "0") Integer warrantyYears,
                            @RequestParam(required = false, defaultValue = "false") Boolean hasGiftWrap,
                            @RequestParam(required = false, defaultValue = "0") BigDecimal discountPercentage,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        try {
            UserSQL user = userOptional.get();
            cartService.addProductToCart(user, productId, quantity, hasWarranty, warrantyYears, hasGiftWrap, discountPercentage);

            Optional<ProductSQL> product = productService.getProductById(productId);
            String productName = product.map(ProductSQL::getName).orElse("item");
            redirectAttributes.addFlashAttribute("addedToCart",
                    "Successfully added " + quantity + " " + productName + " to cart.");
        } catch (CartService.ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }
        CartSQL cart = cartService.getOrCreateCart(userOptional.get());

        // Convert cart items to decorated products for price calculation
        List<product.IProduct> decoratedProducts = cart.getItems().stream()
                .map(product.ProductFactory::createFromCartItem)
                .collect(java.util.stream.Collectors.toList());

        // Calculate grand total
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (int i = 0; i < decoratedProducts.size(); i++) {
            BigDecimal lineTotal = decoratedProducts.get(i).getPrice()
                    .multiply(BigDecimal.valueOf(cart.getItems().get(i).getQuantity()));
            grandTotal = grandTotal.add(lineTotal);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("decoratedProducts", decoratedProducts);
        model.addAttribute("grandTotal", grandTotal);
        return "cart";
    }

    @GetMapping("/payment-methods")
    public String viewPaymentMethods(HttpSession session, Model model) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }
        List<PaymentMethodSQL> paymentMethods =
                paymentMethodService.getUserPaymentMethods(userOptional.get());
        model.addAttribute("paymentMethods", paymentMethods);
        return "payment-methods";
    }

    @GetMapping("/payment/add")
    public String showAddPaymentMethodForm(HttpSession session) {
        if (getCurrentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        return "add-payment-method";
    }

    @PostMapping("/payment/add")
    public String addPaymentMethod(@RequestParam String paymentType, @RequestParam BigDecimal balance, @RequestParam(required = false) String paymentEmail, @RequestParam(required = false) String paymentPassword, @RequestParam(required = false) String paymentCardNumber, @RequestParam(required = false) String paymentCardPin, @RequestParam(required = false) String paymentCardName, HttpSession session, Model model) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        try {
            paymentMethodService.addPaymentMethod(userOptional.get(), paymentType, balance, paymentEmail, paymentPassword, paymentCardNumber, paymentCardPin, paymentCardName);
            return "redirect:/payment-methods";
        } catch (PaymentMethodService.PaymentMethodException e) {
            model.addAttribute("error", e.getMessage());
            return "add-payment-method";
        }
    }

    @PostMapping("/payment/delete")
    public String deletePaymentMethod(@RequestParam Long methodId, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        try {
            paymentMethodService.deletePaymentMethod(userOptional.get(), methodId);
        } catch (PaymentMethodService.PaymentMethodException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/payment-methods";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private Optional<UserSQL> getCurrentUser(HttpSession session) {
        UserSQL user = (UserSQL) session.getAttribute("user");
        return Optional.ofNullable(user);
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        CartSQL cart = cartService.getOrCreateCart(userOptional.get());
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Convert cart items to decorated products for price calculation
        List<product.IProduct> decoratedProducts = cart.getItems().stream()
                .map(product.ProductFactory::createFromCartItem)
                .collect(java.util.stream.Collectors.toList());

        // Calculate grand total
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (int i = 0; i < decoratedProducts.size(); i++) {
            BigDecimal lineTotal = decoratedProducts.get(i).getPrice()
                    .multiply(BigDecimal.valueOf(cart.getItems().get(i).getQuantity()));
            grandTotal = grandTotal.add(lineTotal);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("decoratedProducts", decoratedProducts);
        model.addAttribute("grandTotal", grandTotal);
        return "checkout-customize";
    }

    @PostMapping("/checkout/preview")
    public String previewCheckout(@RequestParam Map<String, String> params, HttpSession session, Model model) {
        Optional<UserSQL> userOptional = getCurrentUser(session);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        Map<Long, CheckoutService.ItemCustomization> customizations = parseCustomizations(params);

        session.setAttribute("customizations", customizations);

        CheckoutResult result = checkoutService.previewCheckout(userOptional.get(), customizations);

        List<PaymentMethodSQL> paymentMethods = paymentMethodService.getUserPaymentMethods(userOptional.get());

        model.addAttribute("checkoutResult", result);
        model.addAttribute("paymentMethods", paymentMethods);

        return "checkout-confirm";
    }

    @PostMapping("/checkout/complete")
    public String completeCheckout(@RequestParam Long paymentMethodId, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<UserSQL> userOptional = getCurrentUser(session);

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        try {
            @SuppressWarnings("unchecked")
            Map<Long, CheckoutService.ItemCustomization> customizations = (Map<Long, CheckoutService.ItemCustomization>) session.getAttribute("customizations");

            if (customizations == null) {
                customizations = new java.util.HashMap<>();
            }

            CheckoutResult result = checkoutService.prepareCheckout(userOptional.get(), customizations);
            checkoutService.completeCheckout(userOptional.get(), paymentMethodId, result);

            session.removeAttribute("customizations");
            redirectAttributes.addFlashAttribute("orderTotal", result.getTotal());
            return "redirect:/order-success";

        } catch (CheckoutService.CheckoutException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-success")
    public String orderSuccess(HttpSession session, Model model) {
        if (getCurrentUser(session).isEmpty()) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("orderTotal")) {
            return "redirect:/dashboard";
        }

        return "order-success";
    }

    private Map<Long, CheckoutService.ItemCustomization> parseCustomizations(Map<String, String> params) {
        Map<Long, CheckoutService.ItemCustomization> customizations = new java.util.HashMap<>();

        params.forEach((key, value) -> {
            if (key.startsWith("giftWrap_")) {
                Long itemId = Long.parseLong(key.substring(9));
                customizations.computeIfAbsent(itemId, k -> new CheckoutService.ItemCustomization()).setGiftWrap(true);
            } else if (key.startsWith("warranty_")) {
                Long itemId = Long.parseLong(key.substring(9));
                int years = Integer.parseInt(value);
                customizations.computeIfAbsent(itemId, k -> new CheckoutService.ItemCustomization()).setWarrantyYears(years);
            } else if (key.startsWith("discount_")) {
                Long itemId = Long.parseLong(key.substring(9));
                BigDecimal percentage = new BigDecimal(value);
                customizations.computeIfAbsent(itemId, k -> new CheckoutService.ItemCustomization()).setDiscountPercentage(percentage);
            }
        });

        return customizations;
    }
}