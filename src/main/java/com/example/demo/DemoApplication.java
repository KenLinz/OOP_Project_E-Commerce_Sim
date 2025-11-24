package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// --- THESE ARE THE IMPORTS YOU ARE MISSING ---
import org.springframework.stereotype.Controller; // <-- This fixes "@Controller"
import org.springframework.web.bind.annotation.ResponseBody; // <-- This fixes "@ResponseBody"
// --- END OF CRITICAL IMPORTS ---

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    // --- ADD THESE NEW METHODS ---

    @GetMapping("/")
    @ResponseBody
    public String index() {
        // This isn't strictly needed because Spring Boot will serve index.html
        // automatically, but it's good practice if you want to point to the root.
        // For this demo, we'll let the static file server handle it.
        // You could also return a redirect: return "redirect:/index.html";
        return "Visit /index.html or the root to see the buttons.";
    }

    // This is the updated method for /2
    @GetMapping("/2")
    public String page2() {
        // This tells Spring Boot to find and render the
        // template named "page2.html" from the 'templates' folder.
        return "page2";
    }

    @GetMapping("/1")
    @ResponseBody // <-- Add this
    public String page1() {
        return "This is Page 1";
    }

    @GetMapping("/3")
    @ResponseBody // <-- Add this
    public String page3() {
        return "This is Page 3";
    }

    @GetMapping("/4")
    @ResponseBody // <-- Add this
    public String page4() {
        return "This is Page 4";
    }
}