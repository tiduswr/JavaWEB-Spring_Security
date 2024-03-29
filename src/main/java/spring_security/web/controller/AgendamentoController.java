package spring_security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring_security.domain.Agendamento;
import spring_security.domain.Especialidade;
import spring_security.domain.Paciente;
import spring_security.domain.PerfilTipo;
import spring_security.service.AgendamentoService;
import spring_security.service.EspecialidadeService;
import spring_security.service.PacienteService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService service;
    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private EspecialidadeService especialidadeService;

    @PreAuthorize("hasAnyAuthority('MEDICO', 'PACIENTE')")
    @GetMapping("/agendar")
    public String agendar(Agendamento agendamento){
        return "agendamento/cadastro";
    }

    @PreAuthorize("hasAnyAuthority('MEDICO', 'PACIENTE')")
    @GetMapping("/horario/medico/{idMedico}/data/{data}")
    public ResponseEntity<?> getHorarios(@PathVariable("idMedico") Long idMedico,
                                         @PathVariable("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return ResponseEntity.ok(service.buscarHorariosDisponiveis(idMedico, date));
    }

    @PreAuthorize("hasAuthority('PACIENTE')")
    @PostMapping("/salvar")
    public String salvar(Agendamento agendamento, RedirectAttributes attr,
                         @AuthenticationPrincipal User user){

        Paciente paciente = pacienteService.buscarPorUsuarioEmail(user.getUsername());
        String titulo = agendamento.getEspecialidade().getTitulo();
        Optional<Especialidade> especialidade = especialidadeService.buscarPorTitulo(titulo);

        if(especialidade.isPresent()) {
            agendamento.setEspecialidade(especialidade.get());
            agendamento.setPaciente(paciente);
            service.salvar(agendamento);
            attr.addFlashAttribute("sucesso", "Sua consulta foi agendada com Sucesso!");
        }else{
            attr.addFlashAttribute("falha", "Especialidade não encontrada!");
        }
        return "redirect:/agendamentos/agendar";
    }

    @PreAuthorize("hasAnyAuthority('MEDICO', 'PACIENTE')")
    @GetMapping({"/historico/paciente", "/historico/consultas"})
    public String historico(){
        return "agendamento/historico-paciente";
    }

    @PreAuthorize("hasAnyAuthority('MEDICO', 'PACIENTE')")
    @GetMapping("/datatables/server/historico")
    public ResponseEntity<?> historicoAgendamentosPorPaciente(HttpServletRequest request, @AuthenticationPrincipal User user){
        if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.PACIENTE.getDesc()))){
            return ResponseEntity.ok(service.buscarHistoricoPorPacienteEmail(user.getUsername(), request));
        }else if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.MEDICO.getDesc()))){
            return ResponseEntity.ok(service.buscarHistoricoPorMedicoEmail(user.getUsername(), request));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('MEDICO', 'PACIENTE')")
    @GetMapping("/editar/consulta/{id}")
    public String preEditarConsultaPaciente(@PathVariable("id") Long id, ModelMap map,
                                            @AuthenticationPrincipal User user){
        Agendamento agendamento = service.buscarPorIdEUsuario(id, user.getUsername());
        map.addAttribute("agendamento", agendamento);
        return "agendamento/cadastro";
    }

    @PreAuthorize("hasAnyAuthority('MEDICO', 'PACIENTE')")
    @PostMapping("/editar")
    public String editarConsulta(Agendamento agendamento, RedirectAttributes attr,
                                 @AuthenticationPrincipal User user){
        String titulo = agendamento.getEspecialidade().getTitulo();
        Optional<Especialidade> especialidade = especialidadeService.buscarPorTitulo(titulo);

        if(especialidade.isPresent()){
            agendamento.setEspecialidade(especialidade.get());
            service.editar(agendamento, user.getUsername());
            attr.addFlashAttribute("sucesso", "Sua consulta foi alterada com Sucesso!");
        }else{
            attr.addFlashAttribute("falha", "Especialidade não encontrada!");
        }

        return "redirect:/agendamentos/agendar";
    }

    @PreAuthorize("hasAnyAuthority('PACIENTE')")
    @GetMapping("/excluir/consulta/{id}")
    public String excluirConsulta(@PathVariable("id") Long id, RedirectAttributes attr){
        service.remover(id);
        attr.addFlashAttribute("sucesso", "Consulta excluida com Sucesso!");
        return "redirect:/agendamentos/historico/paciente";
    }

}
