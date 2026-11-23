package br_com_savepoint.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // autenticação (público)
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/nome-disponivel").permitAll()

                        // páginas e arquivos do front (público)
                        .requestMatchers("/", "/index.html", "/login.html", "/*.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/assets/**", "/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()

                        // swagger (público)
                        .requestMatchers("/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // lista de e-mails de quem favoritou um jogo: só ADMIN
                        .requestMatchers(HttpMethod.GET, "/jogos/*/usuarios-interessados").hasRole("ADMIN")

                        // catálogo, lojas e preços: pública
                        .requestMatchers(HttpMethod.GET, "/jogos/**", "/lojas/**", "/ofertas/**").permitAll()

                        // qualquer usuário logado pode publicar uma avaliação
                        .requestMatchers(HttpMethod.POST, "/jogos/*/avaliacoes").authenticated()

                        // manutenção do catálogo, lojas e ofertas: somente ADMIN
                        .requestMatchers(HttpMethod.POST, "/jogos/**", "/lojas/**", "/ofertas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/jogos/**", "/lojas/**", "/ofertas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/jogos/**", "/lojas/**", "/ofertas/**").hasRole("ADMIN")

                        // usuário logado
                        .requestMatchers(HttpMethod.GET, "/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/usuarios/*").authenticated()
                        .requestMatchers("/usuarios/*/lista-desejos/**").authenticated()

                        //permite a criação de usuários com permissão de ADMIN
                        .requestMatchers(HttpMethod.POST, "/usuarios/admin").hasRole("ADMIN")

                        // demais rotas de usuários: somente ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
