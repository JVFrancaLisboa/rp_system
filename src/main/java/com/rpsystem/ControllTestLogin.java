package com.rpsystem;

import com.rpsystem.domain.inventory.repository.LoteBlankItemRepository;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import com.rpsystem.domain.inventory.model.LoteBlankItem;

@Controller
public class ControllTestLogin {

    private final LoteBlankItemRepository loteBlankItemRepository;

    public ControllTestLogin(LoteBlankItemRepository loteBlankItemRepository) {
        this.loteBlankItemRepository = loteBlankItemRepository;
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
    public String getViewMontagemPecas(Model model){
        List<LoteBlankItem> blanksDisponiveis = loteBlankItemRepository.findAllDisponiveisOrderByEntradaAsc();
        model.addAttribute("blanksDisponiveis", blanksDisponiveis);
        model.addAttribute("temBlanksDisponiveis", !blanksDisponiveis.isEmpty());
        return "montagem-pecas";
    }
}
