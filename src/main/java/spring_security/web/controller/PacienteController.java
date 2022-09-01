package spring_security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring_security.domain.Paciente;
import spring_security.domain.Usuario;
import spring_security.service.PacienteService;
import spring_security.service.UsuarioService;

@Controller
@RequestMapping("pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    @Autowired
    private UsuarioService userService;

    @GetMapping("/dados")
    public String cadastrar(Paciente paciente, ModelMap map, @AuthenticationPrincipal User user){
        paciente = service.buscarPorUsuarioEmail(user.getUsername());
        if(paciente.hasNotId()) paciente.setUsuario(new Usuario(user.getUsername()));
        map.addAttribute("paciente", paciente);
        return "paciente/cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(Paciente paciente, ModelMap map, @AuthenticationPrincipal User user){
        Usuario u = userService.buscarPorEmail(user.getUsername());
        if(UsuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())){
            paciente.setUsuario(u);
            service.salvar(paciente);
            map.addAttribute("sucesso", "Seus dados foram inseridos com sucesso!");
        }else{
            map.addAttribute("falha", "Sua senha não confere, tente novamente!");
        }
        return "paciente/cadastro";
    }

    @PostMapping("/editar")
    public String editar(Paciente paciente, ModelMap map, @AuthenticationPrincipal User user){
        Usuario u = userService.buscarPorEmail(user.getUsername());
        if(UsuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())){
            service.editar(paciente);
            map.addAttribute("sucesso", "Seus dados foram editados com sucesso!");
        }else{
            map.addAttribute("falha", "Sua senha não confere, tente novamente!");
        }
        return "paciente/cadastro";
    }

}
