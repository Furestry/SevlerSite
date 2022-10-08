package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private UserRepository userRepository;

    private PasswordEncoder encoder;

    @GetMapping
    public String registerForm(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", error);

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping
    public void registerSubmit(@ModelAttribute User user, HttpServletResponse response) throws IOException {
        System.out.println(user);

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        System.out.println(user.getPassword());

        response.sendRedirect("/login");
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

}
