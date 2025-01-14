package org.example.postapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rival
 * @since 2025-01-14
 */

@RestController
@RequestMapping("sample")
public class SampleController {

    

    @GetMapping("")
    public String sample(){
        return "Hello World";
    }
}
