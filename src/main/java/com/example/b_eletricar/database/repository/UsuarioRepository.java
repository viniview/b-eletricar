package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findBySubId(String subId);

    // Optional<Usuario> findById(Long id);

    long countByRole(Roles role);

    List<Usuario> findAllByOrderByNomeAsc();

}
