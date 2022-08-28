package spring_security.web.controller;

import groovy.transform.AutoClone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring_security.domain.Perfil;
import spring_security.domain.PerfilTipo;
import spring_security.domain.Usuario;
import spring_security.service.UsuarioService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("u")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping({"/novo/cadastro/usuario"})
    public String cadastroPorAdminParaAdminMedicoPaciente(Usuario u){
        return "usuario/cadastro";
    }

    @GetMapping("/lista")
    public String listarUsuarios(){
        return "usuario/lista";
    }

    @GetMapping("/datatables/server/usuarios")
    public ResponseEntity<?> listarUsuariosDataTables(HttpServletRequest request){
        return ResponseEntity.ok(service.buscarTodos(request));
    }

    @PostMapping("/cadastro/salvar")
    public String salvarUsuarioPeloAdministrador(Usuario user, RedirectAttributes attr){
        List<Perfil> perfis = user.getPerfis();
        final List<Perfil> ADMIN_PACIENTE = Arrays.asList(PerfilTipo.ADMIN.buildPerfil(),
                PerfilTipo.PACIENTE.buildPerfil());
        final List<Perfil> MEDICO_PACIENTE = Arrays.asList(PerfilTipo.MEDICO.buildPerfil(),
                PerfilTipo.PACIENTE.buildPerfil());

        if(perfis.size() > 2 || perfis.containsAll(ADMIN_PACIENTE) || perfis.containsAll(MEDICO_PACIENTE)){
            attr.addFlashAttribute("falha",
                    "Paciente não pode ser ADMIN e/ou Médico");
            attr.addFlashAttribute("usuario", user);
        }else{
            try{
                service.salvarUsuario(user);
                attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");
            }catch(DataIntegrityViolationException sqlException){
                attr.addFlashAttribute("falha",
                        "Cadastrado não realizado, usuário já existe!");
            }
        }
        return "redirect:/u/novo/cadastro/usuario";
    }

    @GetMapping("/editar/credenciais/usuario/{id}")
    public ModelAndView preEditarCredenciais(@PathVariable("id") Long id){
        Optional<Usuario> user = service.buscarPorId(id);
        if(user.isPresent()){
            return new ModelAndView("usuario/cadastro",
                    "usuario", user.get());
        }else{
            ModelAndView error = new ModelAndView("error");
            error.addObject("status", HttpStatus.NOT_FOUND.value());
            error.addObject("error", "Usuário não encontrado!");
            error.addObject("message",
                    "O id do usuário não foi encontrado na base de dados do servidor!");
            return error;
        }
    }

    public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long userID,
                                                       @PathVariable("perfis") Long[] perfisId){
        Usuario us = service.buscarPorIdEPerfis(userID, perfisId);

        if(us.getPerfis().contains(PerfilTipo.ADMIN.buildPerfil()) &&
            !us.getPerfis().contains(PerfilTipo.MEDICO.buildPerfil())){
            return new ModelAndView("usuario/cadastro", "usuario", us);
        }else if(us.getPerfis().contains(PerfilTipo.MEDICO.buildPerfil())){
            return new ModelAndView("especialidade/especialidade");
        }else if(us.getPerfis().contains(PerfilTipo.PACIENTE.buildPerfil())){
            ModelAndView error = new ModelAndView("error");
            error.addObject("status", HttpStatus.FORBIDDEN.value());
            error.addObject("error", "Área Restrita!");
            error.addObject("message",
                    "Os dados de pacientes são restrito à ele!");
            return error;
        }

        return new ModelAndView("redirect:/u/lista");
    }

}
