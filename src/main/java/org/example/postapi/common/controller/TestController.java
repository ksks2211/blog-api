package org.example.postapi.common.controller;

import org.example.postapi.common.handler.PostNotFoundException;
import org.example.postapi.user.exception.AppUserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rival
 * @since 2025-01-14
 */


@RestController
@RequestMapping("/test")
public class TestController {


    @Value("${spring.profiles.active:no-profile}")
    private String profile;


    @GetMapping("")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/profile")
    public String profile() {
        return profile;
    }



    @GetMapping("/runtime")
    public void runtimeException() {
        throw new RuntimeException("Test Runtime Exception");
    }

    @GetMapping("/illegal")
    public void illegalArgs() {
        throw new IllegalArgumentException("Test Illegal Args");
    }


    @GetMapping("/not-found")
    public void notFound() {
        throw new AppUserNotFoundException("Test");
    }

}
