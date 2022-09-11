package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_security.datatables.Datatables;
import spring_security.datatables.DatatablesColunas;
import spring_security.domain.Agendamento;
import spring_security.domain.Horario;
import spring_security.repository.AgendamentoRepository;
import spring_security.repository.projection.HistoricoPaciente;
import spring_security.web.exception.AcessoNegadoException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository repo;
    @Autowired
    private Datatables datatables;

    @Transactional(readOnly = true)
    public List<Horario> buscarHorariosDisponiveis(Long idMedico, LocalDate date) {
        return repo.findHorariosAvailable(idMedico, date);
    }

    @Transactional(readOnly = false)
    public void salvar(Agendamento agendamento) {
        repo.save(agendamento);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarHistoricoPorPacienteEmail(String email, HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
        Page<HistoricoPaciente> page = repo.findHistoricoByPacienteEmail(email, datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarHistoricoPorMedicoEmail(String email, HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
        Page<HistoricoPaciente> page = repo.findHistoricoByMedicoEmail(email, datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = true)
    public Optional<Agendamento> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = false)
    public void editar(Agendamento ag, String username) {
        Agendamento bde = buscarPorIdEUsuario(ag.getId(), username);
        bde.setDataConsulta(ag.getDataConsulta());
        bde.setEspecialidade(ag.getEspecialidade());
        bde.setHorario(ag.getHorario());
        bde.setMedico(ag.getMedico());
    }

    @Transactional(readOnly = true)
    public Agendamento buscarPorIdEUsuario(Long id, String email) {
        return repo.fidByIdAndPacienteOrMedicoEmail(id, email)
                .orElseThrow(() -> new AcessoNegadoException("Acesso negado ao usuário: " + email));
    }

    @Transactional(readOnly = false)
    public void remover(Long id) {
        repo.deleteById(id);
    }
}
