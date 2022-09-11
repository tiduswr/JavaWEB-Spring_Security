package spring_security.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring_security.domain.Agendamento;
import spring_security.domain.Horario;
import spring_security.repository.projection.HistoricoPaciente;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    @Query("SELECT h FROM Horario h " +
            "WHERE NOT EXISTS(" +
                "SELECT a.horario.id FROM Agendamento a " +
                "WHERE a.medico.id = :idMedico AND " +
                "a.dataConsulta = :date AND " +
                "a.horario.id = h.id" +
            ") ORDER BY h.horaMinuto ASC")
    List<Horario> findHorariosAvailable(@Param("idMedico") Long idMedico, @Param("date") LocalDate date);

    @Query("SELECT a.id as id, a.paciente as paciente, CONCAT(a.dataConsulta, ' ', a.horario.horaMinuto) as dataConsulta, " +
            "a.medico as medico, a.especialidade as especialidade FROM Agendamento a " +
            "WHERE a.paciente.usuario.email LIKE :email")
    Page<HistoricoPaciente> findHistoricoByPacienteEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT a.id as id, a.paciente as paciente, CONCAT(a.dataConsulta, ' ', a.horario.horaMinuto) as dataConsulta, " +
            "a.medico as medico, a.especialidade as especialidade FROM Agendamento a " +
            "WHERE a.medico.usuario.email LIKE :email")
    Page<HistoricoPaciente> findHistoricoByMedicoEmail(String email, Pageable pageable);

    @Query("SELECT a FROM Agendamento a " +
            "WHERE (a.id = :id AND a.paciente.usuario.email LIKE :email) " +
            "OR (a.id = :id AND a.medico.usuario.email LIKE :email)")
    Optional<Agendamento> fidByIdAndPacienteOrMedicoEmail(@Param("id") Long id, @Param("email") String email);
}
