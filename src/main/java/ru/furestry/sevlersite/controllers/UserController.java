package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.furestry.sevlersite.entities.db.Comment;
import ru.furestry.sevlersite.repositories.CommentsInMemoryRepository;
import ru.furestry.sevlersite.entities.EventDto;
import ru.furestry.sevlersite.entities.db.Role;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.interfaces.CommentRepository;
import ru.furestry.sevlersite.repositories.interfaces.UserRepository;
import ru.furestry.sevlersite.services.interfaces.INotificationService;
import ru.furestry.sevlersite.services.notifications.CommentsNotificationService;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private CommentsInMemoryRepository commentsInMemoryRepository;
    private INotificationService notificationService;

    @GetMapping
    public String getUsersPage(Model model, Principal principal) {
        model.addAttribute("users", userRepository.findAll());

        if (!checkIfNull(principal)) {
            User user = userRepository.findByUsername(principal.getName());

            model.addAttribute("principalId", user.getId());
        }

        return "usersList";
    }

    @Transactional
    @GetMapping("/id/{id}")
    public String getUserById(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findById(id).orElse(null);

        model.addAttribute("user", user);

        if (!checkIfNull(user)) {
            if (!checkIfNull(user.getAvatar())) {
                model.addAttribute("userAvatar", Base64.getEncoder().encodeToString(user.getAvatar()));
            }

            List<String> userRoles = user.getRoles().stream().map(Role::getName).toList();

            if (userRoles.contains("ROLE_ADMIN")) {
                model.addAttribute("isAdmin", true);
            } else {
                model.addAttribute("isAdmin", false);
            }

            model.addAttribute("comments", commentRepository.findAllByUserId(user.getId()));
        }

        if (!checkIfNull(principal)) {
            User principalUser = userRepository.findByUsername(principal.getName());

            model.addAttribute("principalId", principalUser.getId());
        }

        return "user";
    }

    @GetMapping("/{username}")
    public ResponseEntity<String> getUserByUsername(@PathVariable String username, HttpServletResponse response) throws IOException {
        User user = userRepository.findByUsernameIgnoreCase(username);

        if (checkIfNull(user)) {
            return ResponseEntity.notFound().build();
        }

        response.sendRedirect("/users/id/" + user.getId());

        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @PutMapping("/comments")
    public ResponseEntity<String> createComment(@RequestParam Long userId, @RequestBody String text, Principal principal) {
        User user = userRepository.findById(userId).orElse(null);

        if (checkIfNull(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (checkIfNull(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User author = userRepository.findByUsername(principal.getName());

        LocalDateTime time = LocalDateTime.now();

        EventDto event = new EventDto();
        event.setType("createComment");

        Comment comment = new Comment();

        comment.setAuthorId(author.getId());
        comment.setAuthorAvatar(author.getAvatar());
        comment.setUser(user);
        comment.setText(text);
        comment.setCommentedAt(time);

        commentRepository.save(comment);

        event.setBody(new HashMap<>(){{
            put("date", time);
            put("id", comment.getId());
            put("userId", user.getId());
            put("authorId", author.getId());
            put("username", author.getUsername());
            put("avatar", author.getAvatar());
            put("text", text);
        }});

        commentsInMemoryRepository.getAllIds().forEach(id -> notificationService.sendUpdate(id, event));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments")
    public ResponseEntity<String> editComment(@RequestParam Long commentId, @RequestBody String text, Principal principal) {
        Comment comment = findComment(commentId);

        if (checkIfNull(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (checkIfNull(comment)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        LocalDateTime time = LocalDateTime.now();

        EventDto event = new EventDto();
        event.setType("editComment");
        event.setBody(new HashMap<>(){{
            put("commentId", comment.getId());
            put("userId", comment.getUser().getId());
            put("text", text);
            put("date", time);
        }});

        comment.setText(text);
        comment.setCommentedAt(time);
        comment.setEdited(true);
        commentRepository.save(comment);

        commentsInMemoryRepository.getAllIds().forEach(id -> notificationService.sendUpdate(id, event));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments")
    public ResponseEntity<String> deleteComment(@RequestParam Long commentId, Principal principal) {
        Comment comment = findComment(commentId);

        if (checkIfNull(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (checkIfNull(comment)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        EventDto event = new EventDto();
        event.setType("deleteComment");
        event.setBody(new HashMap<>(){{
            put("commentId", comment.getId());
            put("userId", comment.getUser().getId());
        }});

        commentRepository.delete(comment);

        commentsInMemoryRepository.getAllIds().forEach(id -> notificationService.sendUpdate(id, event));

        return ResponseEntity.ok().build();
    }

    private boolean checkIfNull(Object obj) {
        return obj == null;
    }

    private Comment findComment(long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setNotificationService(CommentsNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Autowired
    public void setUserCommentsRepository(CommentsInMemoryRepository commentsInMemoryRepository) {
        this.commentsInMemoryRepository = commentsInMemoryRepository;
    }
}
