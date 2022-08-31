package spring_security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring_security.domain.Especialidade;
import spring_security.service.EspecialidadeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService service;

    @GetMapping({"", "/"})
    public String abrir(Especialidade especialidade){
        return "especialidade/especialidade";
    }

    @PostMapping("/salvar")
    public String salvar(Especialidade especialidade, RedirectAttributes attr){
        service.salvar(especialidade);
        attr.addFlashAttribute("success", "Operação realizada com sucesso!");
        return "redirect:/especialidades";
    }

    @GetMapping("/datatables/server")
    public ResponseEntity<?> getEspecialidades(HttpServletRequest request){
        return ResponseEntity.ok(service.buscarEspecialidades(request));
    }

    @GetMapping("/titulo")
    public ResponseEntity<?> getEspecialidadesPorTermo(@RequestParam("termo") String termo){
        return ResponseEntity.ok(service.buscarEspecialidadeByTermo(termo));
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap map){
        Optional<Especialidade> especialidade = service.buscarPorID(id);
        especialidade.ifPresent(value -> map.addAttribute("especialidade", value));
        return "especialidade/especialidade";
    }

    @GetMapping("/excluir/{id}")
    public String preEditar(@PathVariable("id") Long id, RedirectAttributes attr){
        service.excluirPorID(id);
        attr.addFlashAttribute("sucesso", "Operação realizada com Sucesso!");
        return "redirect:/especialidades";
    }

    @GetMapping("/datatables/server/medico/{id}")
    public ResponseEntity<?> getEspecialidadePorMedico(@PathVariable("id") Long id, HttpServletRequest request){
        return ResponseEntity.ok(service.buscarEspecialidadesPorMedico(id, request));
    }

}
