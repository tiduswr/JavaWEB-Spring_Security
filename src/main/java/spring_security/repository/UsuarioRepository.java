package spring_security.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring_security.domain.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email like :email")
    Usuario findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM Usuario u " +
            "JOIN u.perfis p " +
            "WHERE u.email LIKE :search% OR p.desc like :search%")
    Page<Usuario> findByEmailOrPerfil(@Param("search") String search, Pageable pageable);

    @Query("SELECT DISTINCT u FROM Usuario u " +
            "JOIN u.perfis p " +
            "WHERE u.id = :userID")
    Optional<Usuario> findByIdAndPerfis(@Param("userID") Long userID);
}
