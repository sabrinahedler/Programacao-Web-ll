package br_com_savepoint.service;

import br_com_savepoint.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // usuário pode entrar com o e-mail ou com o nickname
    // primeiro procura por email, se não achar, procura pelo nome

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String valor = login == null ? "" : login.trim();

        return usuarioRepository.findByEmail(valor)
                .or(() -> usuarioRepository.findByNomeIgnoreCase(valor))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + valor));
    }
}