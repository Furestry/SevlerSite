package ru.furestry.sevlersite.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.furestry.sevlersite.entities.db.Comment;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.CommentRepository;
import ru.furestry.sevlersite.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
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

        private CommentRepository commentRepository;

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

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(new APIUser(userId, user.getUsername()));
        }

        @GetMapping(value = "/users/me")
        public ResponseEntity<APIUser> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            User user = userRepository.findByTokenHash(token);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.ok(new APIUser(user.getId(), user.getUsername()));
        }

        @GetMapping(value = "/comments")
        public List<Comment> getAllComments() {
            return commentRepository.findAll();
        }

        @GetMapping(value = "/comments/me")
        public ResponseEntity<Collection<Comment>> getMyComments(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            User user = userRepository.findByTokenHash(token);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.ok(commentRepository.findAllByAuthorId(user.getId()));
        }

        @GetMapping(value = "/comments/{commentId}")
        public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
            Comment comment = commentRepository.findById(commentId).orElse(null);

            if (comment == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(comment);
        }

        @GetMapping(value = "/comments/user/{authorId}")
        public ResponseEntity<Collection<Comment>> getCommentByUserId(@PathVariable Long authorId) {
            Collection<Comment> comments = commentRepository.findAllByAuthorId(authorId);

            if (comments == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(comments);
        }

        @Autowired
        public void setUserRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Autowired
        public void setCommentRepository(CommentRepository commentRepository) {
            this.commentRepository = commentRepository;
        }

        @Data
        @AllArgsConstructor
        private class APIUser {

            private Long id;

            private String username;
        }
    }

}
