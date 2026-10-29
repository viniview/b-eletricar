package com.example.b_eletricar.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.b_eletricar.database.model.Usuario;
import com.example.b_eletricar.database.model.enums.Roles;
import com.example.b_eletricar.database.repository.UsuarioRepository;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.nome:Administrador}")
    private String adminNome;

    public AdminSeeder(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            log.info("Admin inicial não foi configurado (app.admin.email/app.admin.password vazios); seeder ignorado.");
            return;
        }

        if (adminPassword.length() < 8 || adminPassword.length() > 72) {
            log.error("Admin inicial não criado: a senha deve ter de 8 a 72 caracteres (tamanho recebido: {}).",
                    adminPassword.length());
            return;
        }

        String email = adminEmail.trim().toLowerCase();

        Optional<Usuario> existente = repo.findByEmail(email);
        if (existente.isPresent()) {
            Usuario u = existente.get();
            boolean alterou = false;

            if (u.getRole() != Roles.ADMIN) {
                u.setRole(Roles.ADMIN);
                alterou = true;
            }
            if (u.getSenha() == null) {
                u.setSenha(encoder.encode(adminPassword));
                alterou = true;
            }

            if (alterou) {
                repo.save(u);
                log.info("Admin inicial: usuário existente atualizado com role ADMIN ({}).", email);
            } else {
                log.info("Admin inicial: usuário já existe com role ADMIN ({}); nada alterado.", email);
            }
            return;
        }

        Usuario u = new Usuario();
        u.setNome(adminNome == null || adminNome.isBlank() ? "Administrador" : adminNome.trim());
        u.setEmail(email);
        u.setSenha(encoder.encode(adminPassword));
        u.setRole(Roles.ADMIN);
        repo.save(u);
        log.info("Admin inicial criado com sucesso ({}).", email);
    }
}
