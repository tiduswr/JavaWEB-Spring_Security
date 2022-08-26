package spring_security.web.controller;

import groovy.transform.AutoClone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring_security.domain.Usuario;
import spring_security.service.UsuarioService;

import javax.servlet.http.HttpServletRequest;

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
}
