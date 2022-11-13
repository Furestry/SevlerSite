package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.furestry.sevlersite.entities.db.Role;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.CommentRepository;
import ru.furestry.sevlersite.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private CommentRepository commentRepository;

    @GetMapping("/id/{id}")
    public String getUserById(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findById(id).orElse(null);

        model.addAttribute("user", user);

        if (user.getAvatar() != null) {
            model.addAttribute("userAvatar", Base64.getEncoder().encodeToString(user.getAvatar()));
        }

        List<String> userRoles = user.getRoles().stream().map(Role::getName).toList();

        if (userRoles.contains("ROLE_ADMIN")) {
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }

        if (principal != null) {
            User principalUser = userRepository.findByUsername(principal.getName());

            model.addAttribute("principal", principalUser);
        }

        model.addAttribute("comments", commentRepository.findAllByUserId(user.getId()));

        return "user";
    }

    @GetMapping("/{username}")
    public ResponseEntity<String> getUserByUsername(@PathVariable String username, HttpServletResponse response) throws IOException {
        User user = userRepository.findByUsernameIgnoreCase(username);

        if (user != null) {
            response.sendRedirect("/users/id/" + user.getId());

            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/create_comment")
    public void createComment(@RequestParam Long user_id, @RequestBody String text, Principal principal) {
        long createdAt = Instant.now().getEpochSecond();

    }

    @PostMapping("/edit_comment")
    public void editComment(@RequestParam Long comment_id, @RequestBody String text, Principal principal) {

    }

    @DeleteMapping("/delete_comment")
    public void deleteComment(@RequestParam Long comment_id, Principal principal) {

    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

}
