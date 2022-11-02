package ru.furestry.sevlersite.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ApiController {

    @GetMapping
    public String getApiPage() {
        return "api";
    }

    @RestController
    @RequestMapping("/api/v1")
    public class APIRestController {

        private UserRepository userRepository;

        @GetMapping(value = "/ping")
        public HttpStatus getPing() {
            return HttpStatus.OK;
        }

        @GetMapping(value = "/users")
        public List<APIUser> getAllUsers() {
            List<APIUser> users = new ArrayList<>();

            userRepository.findAll().forEach(user -> {
                APIUser apiUser = new APIUser(user.getId(), user.getUsername());
                users.add(apiUser);
            });

            return users;
        }

        @GetMapping(value = "/users/{userId}")
        public ResponseEntity<APIUser> getUserById(@PathVariable Long userId) {
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                APIUser apiUser = new APIUser(userId, user.getUsername());
                return ResponseEntity.ok(apiUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @Autowired
        public void setUserRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Data
        @AllArgsConstructor
        private class APIUser {

            private Long id;

            private String username;
        }
    }

}
