package br_com_savepoint.infra.security;

import br_com_savepoint.model.UserRoles;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o administrador inicial na subida da aplicação, a partir das variáveis de ambiente
 * ADMIN_EMAIL e ADMIN_PASSWORD. Se não estiverem definidas, ou se o e-mail já existir, não faz nada.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        String email = adminEmail.trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }
        if (adminPassword.length() < 8) {
            log.warn("ADMIN_PASSWORD precisa ter no mínimo 8 caracteres; administrador inicial não foi criado.");
            return;
        }
        usuarioRepository.save(new Usuario("Administrador", email, passwordEncoder.encode(adminPassword), UserRoles.ADMIN));
        log.info("Administrador inicial criado: {}", email);
    }
}
