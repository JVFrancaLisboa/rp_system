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

    @GetMapping("/add-blanks")
    public String getViewAddBlanks(){
        return "add-blanks";
    }

    @GetMapping("/entrada-dtf")
    public String getViewEntradaDtf(){
        return "entrada-dtf";
    }

    @GetMapping("/montagem-pecas")
    public String getViewMontagemPecas(){
        return "montagem-pecas";
    }
}
