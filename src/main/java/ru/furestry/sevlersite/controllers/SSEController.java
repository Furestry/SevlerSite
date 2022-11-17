package ru.furestry.sevlersite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.furestry.sevlersite.entities.db.User;
import ru.furestry.sevlersite.repositories.interfaces.UserRepository;
import ru.furestry.sevlersite.services.emitters.CommentEmitterService;
import ru.furestry.sevlersite.services.emitters.UsersEmitterService;

import java.security.Principal;

@Controller
@RequestMapping("/sse")
public class SSEController {

    private UserRepository userRepository;

    private CommentEmitterService commentEmitterService;
    private UsersEmitterService usersEmitterService;

    @GetMapping("/subscribe/comments")
    public SseEmitter subscribeToComments(Principal principal) {
        if (principal == null) {
            return commentEmitterService.createEmitter();
        }

        User user = userRepository.findByUsername(principal.getName());

        return commentEmitterService.createEmitter(user.getId());
    }

    @GetMapping("/subscribe/users")
    public SseEmitter subscribeToUsers(Principal principal) {
        if (principal == null) {
            return usersEmitterService.createEmitter();
        }

        User user = userRepository.findByUsername(principal.getName());

        return usersEmitterService.createEmitter(user.getId());
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCommentEmitterService(CommentEmitterService commentEmitterService) {
        this.commentEmitterService = commentEmitterService;
    }

    @Autowired
    public void setUsersEmitterService(UsersEmitterService usersEmitterService) {
        this.usersEmitterService = usersEmitterService;
    }

}
