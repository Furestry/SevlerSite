package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UserRepository;

import java.util.Base64;

@Controller
@RequestMapping("/account")
public class AccountController {

    private UserRepository userRepository;

    @GetMapping
    public String getAccountPage(Model model) {
        User user = userRepository.findByUsername("");

        model.addAttribute("user", user);

        if (user.getAvatar() != null) {
            model.addAttribute("userAvatar", Base64.getEncoder().encodeToString(user.getAvatar()));
        }

        return "account";
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
