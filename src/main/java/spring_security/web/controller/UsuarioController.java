package spring_security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

import javax.mail.MessagingException;
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

    @GetMapping("/editar/senha")
    public String abrirEditarSenha(){
        return "usuario/editar-senha";
    }

    @PostMapping("/confirmar/senha")
    public String editarSenha(@RequestParam("senha1") String s1, @RequestParam("senha2") String s2,
                                @RequestParam("senha3") String s3, @AuthenticationPrincipal User user,
                                RedirectAttributes attr){
        Usuario u = service.buscarPorEmail(user.getUsername());

        if(!s1.equals(s2)){
            attr.addFlashAttribute("falha", "Senhas não conferem, tente novamente!");
            return "redirect:/u/editar/senha";
        }else if(!UsuarioService.isSenhaCorreta(s3, u.getSenha())){
            attr.addFlashAttribute("falha", "As senhas não conferem!");
            return "redirect:/u/editar/senha";
        }else if(s3.length() < 5){
            attr.addFlashAttribute("falha", "A senha deve possuir no minimo 5 caracteres!");
            return "redirect:/u/editar/senha";
        }

        service.alterarSenha(u, s1);

        attr.addFlashAttribute("sucesso", "Senha alterada com sucesso!");

        return "redirect:/u/editar/senha";
    }

    @GetMapping("/novo/cadastro")
    public String novoCadastro(Usuario usuario){
        return "cadastrar-se";
    }

    @GetMapping("/cadastro/realizado")
    public String cadastroRealizado(){
        return "fragments/mensagem";
    }

    @PostMapping("/cadastro/paciente/salvar")
    public String salvarCadastroPaciente(Usuario user, BindingResult result) throws MessagingException {
        try{
            service.salvarCadastroPaciente(user);
        }catch(DataIntegrityViolationException ex){
            result.reject("email", "Ops... Este e-mail ja existe na base de dados!");
            return "cadastrar-se";
        }
        return "redirect:/u/cadastro/realizado";
    }

    @GetMapping("/confirmacao/cadastro")
    public String respostaConfirmacaoCadastrPaciente(@RequestParam("codigo") String codigo, RedirectAttributes attr){

        service.ativarCadastroPaciente(codigo);

        attr.addFlashAttribute("alerta", "sucesso");
        attr.addFlashAttribute("titulo", "Cadastro Ativado!");
        attr.addFlashAttribute("texto", "Parabéns, seu cadastro está ativo!");
        attr.addFlashAttribute("subtexto", "Siga com seu login/senha");

        return "redirect:/login";
    }

    @GetMapping("/p/redefinir/senha")
    public String pedidoRedefinirSenha(){
        return "usuario/pedido-recuperar-senha";
    }

    @GetMapping("/p/recuperar/senha")
    public String redefinirSenha(String email, ModelMap map) throws MessagingException {
        service.pedidoRedefinicaoSenha(email);
        map.addAttribute("sucesso", "Em instates você receberá um email para " +
                "prosseguir com a redefinição de sua senha.");
        map.addAttribute("usuario", new Usuario(email));
        return "usuario/recuperar-senha";
    }

    @PostMapping("/p/nova/senha")
    public String confirmacaoDeRedefinicaoDeSenha(Usuario user, ModelMap map){
        Usuario u = service.buscarPorEmail(user.getEmail());
        if(!user.getCodigoVerificador().equals(u.getCodigoVerificador())){
            map.addAttribute("falha", "Código verificador não confere.");
            return "usuario/recuperar-senha";
        }
        u.setCodigoVerificador(null);
        service.alterarSenha(u, user.getSenha());
        map.addAttribute("alerta", "sucesso");
        map.addAttribute("titulo", "Senha redefinida!");
        map.addAttribute("texto", "Você já pode logar no sistema.");
        return "login";
    }

}
