package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.furestry.sevlersite.entities.db.Comment;
import ru.furestry.sevlersite.repositories.UserCommentsRepository;
import ru.furestry.sevlersite.services.EmitterService;
import ru.furestry.sevlersite.entities.EventDto;
import ru.furestry.sevlersite.entities.db.Role;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.interfaces.CommentRepository;
import ru.furestry.sevlersite.repositories.interfaces.UserRepository;
import ru.furestry.sevlersite.services.interfaces.UpdateService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private UserCommentsRepository userCommentsRepository;
    private final EmitterService emitterService;
    private final UpdateService notificationService;

    public UserController(EmitterService emitterService, UpdateService notificationService) {
        this.emitterService = emitterService;
        this.notificationService = notificationService;

        Thread thread = new Thread(() -> {
            EventDto event = new EventDto();
            event.setType("ping");
            event.setBody(null);

            userCommentsRepository.getAllIds().forEach(id -> notificationService.sendUpdate(id, event));

            try {
                Thread.sleep(1000 * 60 * 2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
    }

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

            model.addAttribute("principal", principalUser);
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

        EventDto event = new EventDto();
        event.setType("createComment");

        Comment comment = new Comment();

        comment.setAuthorId(author.getId());
        comment.setUser(user);
        comment.setText(text);
        comment.setCommentedAt(LocalDateTime.now());

        commentRepository.save(comment);

        event.setBody(new HashMap<>(){{
            put("date", LocalDate.now());
            put("id", comment.getId());
            put("userId", user.getId());
            put("username", author.getUsername());
            put("avatar", author.getAvatar());
            put("text", text);
        }});

        userCommentsRepository.getAllIds().forEach(id -> {
            notificationService.sendUpdate(id, event);
        });

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

        EventDto event = new EventDto();
        event.setType("editComment");
        event.setBody(new HashMap<>(){{
            put("commentId", comment.getId());
            put("userId", comment.getUser().getId());
            put("text", text);
        }});

        comment.setText(text);
        commentRepository.save(comment);

        userCommentsRepository.getAllIds().forEach(id -> {
            notificationService.sendUpdate(id, event);
        });

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

        userCommentsRepository.getAllIds().forEach(id -> {
            notificationService.sendUpdate(id, event);
        });

        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribeToEvents(Principal principal) {
        if (principal == null) {
            return emitterService.createEmitter();
        }

        User user = userRepository.findByUsername(principal.getName());

        return emitterService.createEmitter(user.getId());
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
    public void setUserCommentsRepository(UserCommentsRepository userCommentsRepository) {
        this.userCommentsRepository = userCommentsRepository;
    }

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
}
