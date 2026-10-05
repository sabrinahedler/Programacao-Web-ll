package br_com_savepoint;

import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.UsuarioRepository;
import br_com_savepoint.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void deveNormalizarEmailEArmazenarSenhaComHash() {
        UsuarioService service = new UsuarioService(repository, encoder);
        Usuario entrada = new Usuario();
        entrada.setNome("Maria");
        entrada.setEmail(" Maria@EXEMPLO.COM ");
        entrada.setSenha("senha123");
        when(repository.existsByEmail("maria@exemplo.com")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario salvo = service.salvar(entrada);

        assertEquals("maria@exemplo.com", salvo.getEmail());
        assertNotEquals("senha123", salvo.getSenha());
        assertTrue(encoder.matches("senha123", salvo.getSenha()));
        verify(repository).save(any(Usuario.class));
    }
}
