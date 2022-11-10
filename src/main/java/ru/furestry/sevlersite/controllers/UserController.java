package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.CommentRepository;
import ru.furestry.sevlersite.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private CommentRepository commentRepository;

    @GetMapping("/id/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);

        model.addAttribute("user", user);

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

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

}
