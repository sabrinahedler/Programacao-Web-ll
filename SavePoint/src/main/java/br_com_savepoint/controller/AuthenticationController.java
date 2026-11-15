package br_com_savepoint.controller;


import br_com_savepoint.infra.security.TokenService;
import br_com_savepoint.model.*;
import br_com_savepoint.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    // Nickname: 3 a 30 caracteres, letras (com acento), números, ponto, hífen e underline. Sem espaço e sem @.
    private static final Pattern NICKNAME_VALIDO = Pattern.compile("^[\\p{L}0-9_.-]{3,30}$");
    private static final String[] SUFIXOS = {"_gg", "_br", "_pro", "_xp", "_save"};

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody @Valid AuthenticationDTO data) {
        try {
            // O campo "email" do DTO aceita e-mail OU nickname (ver AutenticacaoService)
            String login = data.email() == null ? "" : data.email().trim();
            var usernamePassword = new UsernamePasswordAuthenticationToken(login, data.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            Usuario usuario = (Usuario) auth.getPrincipal();

            var token = tokenService.generateToken(usuario);
            var refreshToken = tokenService.generateRefreshToken(usuario);

            return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken));

        } catch (AuthenticationException e) {
            // Mesma mensagem para usuário inexistente, senha errada ou conta desativada.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login ou senha incorretos.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshToken(@RequestBody @Valid RefreshTokenDTO data) {
        var email = tokenService.validateToken(data.refreshToken());

        if (email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token inválido ou expirado.");
        }

        var usuario = repository.findByEmail(email).orElse(null);
        if (usuario == null || !usuario.isEnabled()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado ou desativado.");
        }

        var novoToken = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(novoToken, data.refreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        String nome = data.nome() == null ? "" : data.nome().trim();
        String email = data.email() == null ? "" : data.email().trim();
        String senha = data.senha() == null ? "" : data.senha();

        if (!NICKNAME_VALIDO.matcher(nome).matches()) {
            return ResponseEntity.badRequest().body("O nickname deve ter de 3 a 30 caracteres: letras, números, ponto, hífen ou underline.");
        }
        if (!email.contains("@")) {
            return ResponseEntity.badRequest().body("Informe um e-mail válido.");
        }
        if (senha.length() < 6) {
            return ResponseEntity.badRequest().body("A senha deve ter no mínimo 6 caracteres.");
        }
        if (this.repository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }
        if (this.repository.existsByNomeIgnoreCase(nome)) {
            return ResponseEntity.badRequest().body("Este nickname já está em uso.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(senha);

        // Todo cadastro público nasce como USER. O campo "role" enviado pelo cliente é ignorado,
        // para ninguém conseguir se cadastrar como ADMIN pelo site.
        Usuario newUsuario = new Usuario(nome, email, encryptedPassword, UserRoles.USER);
        newUsuario.setAtivo(true);
        newUsuario.setDataCadastro(LocalDateTime.now());

        this.repository.save(newUsuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nome-disponivel")
    public ResponseEntity<NomeDisponivelDTO> nomeDisponivel(@RequestParam String nome) {
        String base = nome == null ? "" : nome.trim();

        if (!NICKNAME_VALIDO.matcher(base).matches()) {
            return ResponseEntity.ok(new NomeDisponivelDTO(false, List.of()));
        }
        if (!repository.existsByNomeIgnoreCase(base)) {
            return ResponseEntity.ok(new NomeDisponivelDTO(true, List.of()));
        }
        return ResponseEntity.ok(new NomeDisponivelDTO(false, sugerirNomes(base)));
    }

    private List<String> sugerirNomes(String base) {
        String raiz = base.length() > 24 ? base.substring(0, 24) : base;
        ThreadLocalRandom aleatorio = ThreadLocalRandom.current();
        List<String> sugestoes = new ArrayList<>();

        for (int tentativa = 0; tentativa < 30 && sugestoes.size() < 3; tentativa++) {
            String candidato = switch (tentativa % 3) {
                case 0 -> raiz + aleatorio.nextInt(10, 100);
                case 1 -> raiz + SUFIXOS[aleatorio.nextInt(SUFIXOS.length)];
                default -> raiz + "_" + aleatorio.nextInt(100, 1000);
            };

            boolean repetido = sugestoes.stream().anyMatch(s -> s.equalsIgnoreCase(candidato));
            if (!repetido && candidato.length() <= 30 && !repository.existsByNomeIgnoreCase(candidato)) {
                sugestoes.add(candidato);
            }
        }
        return sugestoes;
    }
}
