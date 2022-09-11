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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
}
