package spring_security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring_security.domain.Medico;

@Controller
@RequestMapping("medicos")
public class MedicoController {

    @GetMapping({"/dados"})
    public String abrirPorMedico(Medico medico, ModelMap map){
        return "medico/cadastro";
    }

}
