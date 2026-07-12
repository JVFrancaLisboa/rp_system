package com.rpsystem;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllTestLogin {

    @GetMapping("/")
    public String getViewLogin(){
        return "login";
    }
}
