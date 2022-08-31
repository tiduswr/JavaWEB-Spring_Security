package spring_security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring_security.domain.Medico;
import spring_security.domain.Usuario;
import spring_security.service.MedicoService;
import spring_security.service.UsuarioService;

@Controller
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoService service;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping({"/dados"})
    public String abrirPorMedico(Medico medico, ModelMap map, @AuthenticationPrincipal User user){
        if(medico.hasNotId()){
            medico = service.buscarPorEmail(user.getUsername());
            map.addAttribute("medico", medico);
        }
        return "medico/cadastro";
    }

    @PostMapping({"/salvar"})
    public String salvar(Medico medico, RedirectAttributes attr,
                         @AuthenticationPrincipal User user){
        if(medico.hasNotId() && medico.getUsuario().hasNotId()){
            Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
            medico.setUsuario(usuario);
        }
        service.salvar(medico);
        attr.addFlashAttribute("sucesso", "Operação realizada com Sucesso!");
        attr.addFlashAttribute("medico", medico);
        return "redirect:/medicos/dados";
    }

    @PostMapping({"/editar"})
    public String editar(Medico medico, RedirectAttributes attr){
        service.editar(medico);
        attr.addFlashAttribute("sucesso", "Operação realizada com Sucesso!");
        attr.addFlashAttribute("medico", medico);
        return "redirect:/medicos/dados";
    }

    @GetMapping({"/id/{idMed}/excluir/especializacao/{idEsp}"})
    public String excluirEspecialidadePorMedico(@PathVariable("idMed") Long idMed,
                                                @PathVariable("idEsp") Long idEsp,
                                                RedirectAttributes attr){
        service.excluirEspecialidadePorMedico(idMed, idEsp);
        attr.addFlashAttribute("sucesso", "Especialidade removida com Sucesso!");
        return "redirect:/medicos/dados";
    }
}
