package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.furestry.sevlersite.entities.ApiToken;
import ru.furestry.sevlersite.entities.EventDto;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UsersInMemoryRepository;
import ru.furestry.sevlersite.repositories.interfaces.RoleRepository;
import ru.furestry.sevlersite.repositories.interfaces.UserRepository;
import ru.furestry.sevlersite.services.interfaces.INotificationService;
import ru.furestry.sevlersite.services.notifications.UsersNotificationService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Value("${api-token.crc32-secret-key}")
    private String crc32SecretKey;

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder encoder;

    private UsersInMemoryRepository usersInMemoryRepository;

    private INotificationService notificationService;

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
        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));

        userRepository.save(user);

        EventDto event = new EventDto();
        event.setType("createUser");
        event.setBody(new HashMap<>(){{
            put("id",user.getId());
            put("avatar", user.getAvatar());
            put("username", user.getUsername());
        }});

        usersInMemoryRepository.getAllIds().forEach(id -> notificationService.sendUpdate(id, event));

        response.sendRedirect("/login");
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setUsersInMemoryRepository(UsersInMemoryRepository usersInMemoryRepository) {
        this.usersInMemoryRepository = usersInMemoryRepository;
    }

    @Autowired
    public void setNotificationService(UsersNotificationService usersNotificationService) {
        this.notificationService = usersNotificationService;
    }

}
