package ru.furestry.sevlersite.controllers;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.furestry.sevlersite.entities.ApiToken;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UserRepository;

import java.security.Principal;
import java.util.Base64;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Value("${api-token.crc32-secret-key}")
    private String crc32SecretKey;

    private UserRepository userRepository;

    private PasswordEncoder encoder;

    @GetMapping
    public String getAccountPage(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);

        if (user.getAvatar() != null) {
            model.addAttribute("userAvatar", Base64.getEncoder().encodeToString(user.getAvatar()));
        }

        return "account";
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePass(@ModelAttribute Password password, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());

        if (!encoder.matches(password.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        user.setPassword(encoder.encode(password.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-token")
    public ResponseEntity<String> changeToken(Principal principal) {
        User user = userRepository.findByUsername(principal.getName());

        String apiTokenValue = ApiToken.createToken(crc32SecretKey);

        user.setToken(apiTokenValue);
        user.setTokenHash(ApiToken.hashToken(apiTokenValue));

        userRepository.save(user);

        return ResponseEntity.ok(user.getTokenHash());
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
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

    @Data
    public class Password {

        private String oldPassword;

        private String newPassword;

    }
}
