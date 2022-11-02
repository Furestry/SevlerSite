package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.furestry.sevlersite.entities.db.ApiToken;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Value("${api-token.crc32-secret-key}")
    private String crc32SecretKey;

    private UserRepository userRepository;

    private PasswordEncoder encoder;

    @GetMapping
    public String registerForm(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", error);

        model.addAttribute("user", new User());

        return "registration";
    }

    @PostMapping
    public void registerSubmit(@ModelAttribute User user, HttpServletResponse response) throws IOException {
        String apiTokenValue = ApiToken.createToken(crc32SecretKey);

        user.setPassword(encoder.encode(user.getPassword()));
        user.setToken(apiTokenValue);
        user.setTokenHash(ApiToken.hashToken(apiTokenValue));

        userRepository.save(user);

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
