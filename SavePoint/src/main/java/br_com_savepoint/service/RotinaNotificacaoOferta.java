package br_com_savepoint.service;

import br_com_savepoint.model.ItemListaDesejos;
import br_com_savepoint.model.OfertaJogo;
import br_com_savepoint.model.Usuario;
import br_com_savepoint.repository.ItemListaDesejosRepository;
import br_com_savepoint.repository.OfertaJogoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RotinaNotificacaoOferta {

    private final OfertaJogoRepository ofertaJogoRepository;
    private final ItemListaDesejosRepository itemListaDesejosRepository;
    private final EmailService emailService;

    public RotinaNotificacaoOferta(OfertaJogoRepository ofertaJogoRepository,
                                   ItemListaDesejosRepository itemListaDesejosRepository,
                                   EmailService emailService) {
        this.ofertaJogoRepository = ofertaJogoRepository;
        this.itemListaDesejosRepository = itemListaDesejosRepository;
        this.emailService = emailService;
    }

    /**
     * a cada minuto, confere cada jogo das listas de desejos:
     * - se o usuário desligou "notificar", pula
     * - se ele informou um preço (precoAlerta > 0), avisa quando o menor preço chegar a esse valor ou menos
     * - se não informou preço (precoAlerta = 0), avisa quando o jogo entrar em qualquer promoção
     */
    @Scheduled(fixedRate = 60000)
    public void verificarOfertasENotificar() {
        System.out.println("Verificando ofertas para as listas de desejos...");

        for (ItemListaDesejos item : itemListaDesejosRepository.findAll()) {
            if (!item.isNotificarOferta() || item.getJogo() == null || item.getListaDesejos() == null) {
                continue;
            }

            Usuario usuario = item.getListaDesejos().getUsuario();
            if (usuario == null || !usuario.isEnabled()) {
                continue;
            }

            List<OfertaJogo> ofertas = ofertaJogoRepository.findByJogoIdOrderByPrecoAtualAsc(item.getJogo().getId());
            if (ofertas.isEmpty()) {
                continue;
            }

            OfertaJogo melhorOferta = ofertas.get(0);
            boolean atingiuAlerta = item.getPrecoAlerta() > 0
                    ? melhorOferta.getPrecoAtual() <= item.getPrecoAlerta()
                    : melhorOferta.getPercentualDesconto() > 0;

            if (atingiuAlerta) {
                emailService.enviarAlertaOferta(
                        usuario.getEmail(),
                        item.getJogo().getTitulo(),
                        melhorOferta.getPrecoAtual()
                );
            }
        }
    }
}
