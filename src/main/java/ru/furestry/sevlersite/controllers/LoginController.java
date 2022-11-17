package ru.furestry.sevlersite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.furestry.sevlersite.configs.handlers.CustomLoginSuccessHandler;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String getLoginPage(HttpServletRequest request) {
        request.getSession().setAttribute(
                CustomLoginSuccessHandler.REDIRECT_URL_SESSION_ATTRIBUTE_NAME,
                request.getHeader("Referer")
        );

        return "login";
    }
}
