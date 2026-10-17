package com.example.b_eletricar.database.repository;

import com.example.b_eletricar.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findBySubId(String subId);

    // Optional<Usuario> findById(Long id);
    
}
