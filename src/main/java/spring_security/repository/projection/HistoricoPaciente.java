package spring_security.repository.projection;

import spring_security.domain.Especialidade;
import spring_security.domain.Medico;
import spring_security.domain.Paciente;

public interface HistoricoPaciente {
    Long getId();
    Paciente getPaciente();
    String getDataConsulta();
    Medico getMedico();
    Especialidade getEspecialidade();
}
