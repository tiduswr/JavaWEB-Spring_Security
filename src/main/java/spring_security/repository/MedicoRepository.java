package spring_security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring_security.domain.Medico;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    @Query("SELECT m FROM Medico m WHERE m.usuario.id = :userID")
    Optional<Medico> findByUsuarioId(@Param("userID") Long userID);

    @Query("SELECT m FROM Medico m WHERE m.usuario.email LIKE :username")
    Optional<Medico> findMedicoByEmail(String username);
}
