package br_com_savepoint.service;

import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.OfertaJogoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RotinaNotificacaoOferta {

    private final OfertaJogoRepository ofertaJogoRepository;
    private final ListaDesejosService listaDesejosService;
    private final EmailService emailService;

    public RotinaNotificacaoOferta(OfertaJogoRepository ofertaJogoRepository, ListaDesejosService listaDesejosService, EmailService emailService) {
        this.ofertaJogoRepository = ofertaJogoRepository;
        this.listaDesejosService = listaDesejosService;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60000)
    public void verificarOfertasENotificar() {
        System.out.println("Verificando ofertas com desconto...");

        List<OfertaJogo> ofertas = ofertaJogoRepository.findAll();

        for (OfertaJogo oferta : ofertas) {
            if (oferta.getPercentualDesconto() > 0 && oferta.getJogo() != null) {
                Long jogoId = oferta.getJogo().getId();
                String nomeJogo = oferta.getJogo().getTitulo();

                List<Usuario> interessados = listaDesejosService.buscarUsuariosInteressados(jogoId);

                for (Usuario usuario : interessados) {
                    if (usuario.getAtivo() != null && usuario.getAtivo()) {
                        emailService.enviarAlertaOferta(
                                usuario.getEmail(),
                                nomeJogo,
                                oferta.getPrecoAtual()
                        );
                    }
                }
            }
        }
    }
}
