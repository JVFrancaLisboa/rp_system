package com.rpsystem.presentation;

import com.rpsystem.domain.inventory.model.LoteBlank;
import com.rpsystem.domain.inventory.model.LoteBlankItem;
import com.rpsystem.application.inventory.LoteBlankService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/lotes")
public class LoteBlankController {

    private final LoteBlankService loteBlankService;

    public LoteBlankController(LoteBlankService loteBlankService) {
        this.loteBlankService = loteBlankService;
    }

    @PostMapping("/blanks")
    public String salvar(@ModelAttribute LoteBlank loteBlank) {
        if (loteBlank.getItens() != null) {
            for (LoteBlankItem item : loteBlank.getItens()) {
                item.setLoteBlank(loteBlank);
            }
        }

        loteBlankService.registrar(loteBlank);
        return "redirect:/";
    }
}