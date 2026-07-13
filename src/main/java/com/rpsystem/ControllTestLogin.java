package com.rpsystem;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllTestLogin {

    @GetMapping("/")
    public String getDash(){
        return "dashboard";
    }

    @GetMapping("/login")
    public String getViewLogin(){
        return "login";
    }

    @GetMapping("/lotes")
    public String getViewLote(){
        return "lote-control";
    }
}
