package com.taskmanager.controller;

import com.taskmanager.model.User;
import com.taskmanager.service.UserService;
import com.taskmanager.service.EmailService;
import com.taskmanager.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordResetTokenService tokenService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            userService.registerUser(user);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    
    // ===== FORGOT PASSWORD FUNCTIONALITY =====
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        return "forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "No account found with that email address.");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/forgot-password";
            }

            String token = tokenService.generateResetToken(user); // ✅ FIXED

            String appUrl = request.getScheme() + "://" + request.getServerName() + 
                          ":" + request.getServerPort() + request.getContextPath();
            String resetUrl = appUrl + "/reset-password?token=" + token;
            
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl, user.getUsername());
            
            redirectAttributes.addFlashAttribute("success", "Password reset instructions have been sent to your email.");
            return "redirect:/forgot-password";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/forgot-password";
        }
    }
    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!tokenService.validateToken(token)) { // ✅ FIXED
            return "redirect:/forgot-password?expired=true";
        }
        
        model.addAttribute("token", token);
        return "reset-password";
    }
    
    @PostMapping("/reset-password")
    public String processPasswordReset(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        if (!tokenService.validateToken(token)) { // ✅ FIXED
            return "redirect:/forgot-password?expired=true";
        }
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long.");
            return "redirect:/reset-password?token=" + token;
        }
        
        try {
            User user = tokenService.getUserByToken(token);
            userService.updatePassword(user, password);
            tokenService.invalidateToken(token); // ✅ FIXED
            
            redirectAttributes.addFlashAttribute("success", "Password reset successful! Please login with your new password.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/reset-password?token=" + token;
        }
    }
}
