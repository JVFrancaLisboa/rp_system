package com.rpsystem.presentation;

import com.rpsystem.application.production.OrdemProducaoService;
import com.rpsystem.presentation.request.OrdemProducaoCreateRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/producao")
public class OrdemProducaoController {

    private final OrdemProducaoService ordemProducaoService;

    public OrdemProducaoController(OrdemProducaoService ordemProducaoService) {
        this.ordemProducaoService = ordemProducaoService;
    }

    @PostMapping(value = "/ordens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registrar(@ModelAttribute OrdemProducaoCreateRequest request) {
        ordemProducaoService.registrar(request);
        return "redirect:/";
    }
}