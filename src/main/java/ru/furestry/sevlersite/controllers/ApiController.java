package ru.furestry.sevlersite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api")
public class ApiController {

    @GetMapping
    public String getTest() {
        return "api";
    }

    @RestController
    @RequestMapping("/api/v1")
    public class APIRestController {

        @GetMapping(value = "/test")
        public String getTest() {
            return "Success test!";
        }

    }

}
