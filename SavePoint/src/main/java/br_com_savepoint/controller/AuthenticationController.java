package br_com_savepoint.controller;

import br_com_savepoint.exception.ErroResposta;
import br_com_savepoint.infra.security.TokenService;
import br_com_savepoint.model.AuthenticationDTO;
import br_com_savepoint.model.LoginResponseDTO;
import br_com_savepoint.model.RegisterDTO;
import br_com_savepoint.model.UserRoles;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager, UsuarioRepository repository,
                                    TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email().trim().toLowerCase(), data.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            Usuario usuario = (Usuario) auth.getPrincipal();

            usuario.marcarAcesso();
            repository.save(usuario);

            String token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", tokenService.getExpirationSeconds(), usuario.getRole().name()));
        } catch (AuthenticationException e) {
            // Mesma mensagem para e-mail inexistente, senha errada ou conta desativada (não revela qual foi).
            return erro(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        String email = data.email().trim().toLowerCase();
        if (this.repository.existsByEmail(email)) {
            return erro(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }

        String encryptedPassword = passwordEncoder.encode(data.senha());
        Usuario novoUsuario = new Usuario(data.nome().trim(), email, encryptedPassword, UserRoles.USER);
        this.repository.save(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private ResponseEntity<ErroResposta> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ErroResposta(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem));
    }
}
