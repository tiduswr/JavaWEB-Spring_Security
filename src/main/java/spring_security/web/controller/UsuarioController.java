package spring_security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring_security.domain.Usuario;

@Controller
@RequestMapping("u")
public class UsuarioController {

    @GetMapping({"/novo/cadastro/usuario"})
    public String cadastroPorAdminParaAdminMedicoPaciente(Usuario u){
        return "usuario/cadastro";
    }

}
