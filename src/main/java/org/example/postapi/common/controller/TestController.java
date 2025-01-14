package org.example.postapi.common.controller;

import org.example.postapi.common.exception.PostNotFoundException;
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


    @GetMapping("")
    public String test() {
        return "Hello World";
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
        throw new PostNotFoundException(12L);
    }

}
