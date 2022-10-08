package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UserRepository;

import java.security.Principal;
import java.util.Base64;

@Controller
@RequestMapping("/account")
public class AccountController {

    private UserRepository userRepository;

    @GetMapping
    public String getAccountPage(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());

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

    @RestController
    @RequestMapping("/account")
    public class AccountAvatar {

        private UserRepository userRepository;

        @PostMapping(value = "/avatar")
        public void uploadAvatar(@RequestParam("file") MultipartFile multipartImage, Principal principal) throws Exception {
            User user = userRepository.findByUsername(principal.getName());
            user.setAvatar(multipartImage.getBytes());

            userRepository.save(user);
        }

        @Autowired
        public void setUserRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
        }
    }
}
