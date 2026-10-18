package br_com_savepoint.infra.security;

import br_com_savepoint.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Gera e valida os tokens JWT usados na autenticação stateless. */
@Service
public class TokenService {

    private static final String ISSUER = "savepoint-api";

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private long expirationHours;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim("role", usuario.getRole() != null ? usuario.getRole().name() : null)
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new IllegalStateException("Erro ao gerar o token JWT.", exception);
        }
    }

    /** Retorna o e-mail (subject) do token, ou null se o token for inválido ou estiver expirado. */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public long getExpirationSeconds() {
        return expirationHours * 3600;
    }

    private Instant generateExpirationDate() {
        return Instant.now().plus(expirationHours, ChronoUnit.HOURS);
    }
}
