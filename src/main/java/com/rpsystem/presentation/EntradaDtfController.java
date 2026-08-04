package com.rpsystem.presentation;

import com.rpsystem.application.finance.EntradaDtfService;
import com.rpsystem.presentation.request.EntradaDtfCreateRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/estoque")
public class EntradaDtfController {

    private final EntradaDtfService entradaDtfService;

    public EntradaDtfController(EntradaDtfService entradaDtfService) {
        this.entradaDtfService = entradaDtfService;
    }

    @PostMapping("/dtf")
    public String registrar(@ModelAttribute EntradaDtfCreateRequest request) {
        entradaDtfService.registrar(request);
        return "redirect:/";
    }
}