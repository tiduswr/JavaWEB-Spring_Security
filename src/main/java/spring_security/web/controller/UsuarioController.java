package spring_security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring_security.domain.Medico;
import spring_security.domain.Perfil;
import spring_security.domain.PerfilTipo;
import spring_security.domain.Usuario;
import spring_security.service.MedicoService;
import spring_security.service.UsuarioService;
import spring_security.web.exception.RestrictedArea;
import spring_security.web.exception.UserNotFound;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("u")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    @Autowired
    private MedicoService medicoService;

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
    public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) throws UserNotFound {
        Optional<Usuario> user = service.buscarPorId(id);
        if(user.isPresent()){
            return new ModelAndView("usuario/cadastro",
                    "usuario", user.get());
        }else{
            throw new UserNotFound("O id do usuário não foi encontrado na base de dados do servidor!");
        }
    }

    @GetMapping("/editar/dados/usuario/{id}")
    public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long userID)
                                                        throws RestrictedArea, UserNotFound {
        Usuario us = service.buscarPorIdEPerfis(userID);

        if(us.getPerfis().contains(PerfilTipo.ADMIN.buildPerfil()) &&
            !us.getPerfis().contains(PerfilTipo.MEDICO.buildPerfil())){
            return new ModelAndView("usuario/cadastro", "usuario", us);

        }else if(us.getPerfis().contains(PerfilTipo.MEDICO.buildPerfil())){

            Medico medico = medicoService.buscarPorUsuarioID(userID);

            return medico.hasNotId()
                    ? new ModelAndView("medico/cadastro", "medico",
                        new Medico(new Usuario(userID)))
                    : new ModelAndView("medico/cadastro", "medico",
                        medico);

        }else if(us.getPerfis().contains(PerfilTipo.PACIENTE.buildPerfil())){
            throw new RestrictedArea("Os dados de pacientes são restrito à ele!");
        }

        return new ModelAndView("redirect:/u/lista");
    }

}
