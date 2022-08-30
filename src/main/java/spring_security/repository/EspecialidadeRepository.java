package spring_security.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring_security.domain.Especialidade;

import java.util.List;
import java.util.Set;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    @Query("SELECT e FROM Especialidade e WHERE e.titulo LIKE :search%")
    Page<?> findAllByTitulo(@Param("search") String search, Pageable pageable);

    @Query("SELECT e.titulo FROM Especialidade e WHERE e.titulo LIKE :termo%")
    List<String> findEspecialidadesByTermo(@Param("termo") String termo);

    @Query("SELECT e FROM Especialidade e WHERE e.titulo IN :source")
    Set<Especialidade> findByTitulos(@Param("source") String[] source);
}
