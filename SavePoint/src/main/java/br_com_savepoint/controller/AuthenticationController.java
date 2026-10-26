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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody @Valid AuthenticationDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            Usuario usuario = (Usuario) auth.getPrincipal();

            var token = tokenService.generateToken(usuario);
            var refreshToken = tokenService.generateRefreshToken(usuario);

            return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
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
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if(this.repository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        Usuario newUsuario = new Usuario(data.nome(), data.email(), encryptedPassword, data.role());

        this.repository.save(newUsuario);

        return ResponseEntity.ok().build();
    }

}
