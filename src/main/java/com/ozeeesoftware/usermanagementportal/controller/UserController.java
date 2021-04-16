package com.ozeeesoftware.usermanagementportal.controller;

import com.ozeeesoftware.usermanagementportal.exception.ExceptionHandling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/","/api/v1/user"})
public class UserController extends ExceptionHandling {

    @GetMapping("/test")
    public String test(){
        return "Application works fine";
    }

}
