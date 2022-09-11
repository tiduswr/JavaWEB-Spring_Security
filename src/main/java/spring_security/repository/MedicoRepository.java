package spring_security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring_security.domain.Medico;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    @Query("SELECT m FROM Medico m WHERE m.usuario.id = :userID")
    Optional<Medico> findByUsuarioId(@Param("userID") Long userID);

    @Query("SELECT m FROM Medico m WHERE m.usuario.email LIKE :username")
    Optional<Medico> findMedicoByEmail(String username);

    @Query("SELECT DISTINCT m FROM Medico m " +
            "JOIN m.especialidades e " +
            "WHERE e.titulo LIKE :titulo " +
            "AND m.usuario.ativo = true")
    List<Medico> findMedicosByEspecialidade(@Param("titulo") String titulo);

    @Query("SELECT m.id FROM Medico m " +
            "JOIN m.especialidades e " +
            "JOIN m.agendamentos a " +
            "WHERE a.especialidade.id = :idEsp AND a.medico.id = :idMed")
    Optional<Long> hasEspecialidadeAgendada(@Param("idMed") Long idMed, @Param("idEsp") Long idEsp);
}
