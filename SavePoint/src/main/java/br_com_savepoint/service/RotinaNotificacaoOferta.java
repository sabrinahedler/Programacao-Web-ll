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

    public RotinaNotificacaoOferta(OfertaJogoRepository ofertaJogoRepository, ItemListaDesejosRepository itemListaDesejosRepository, EmailService emailService) {
        this.ofertaJogoRepository = ofertaJogoRepository;
        this.itemListaDesejosRepository = itemListaDesejosRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void verificarOfertasENotificar() {
        System.out.println("Verificando ofertas...");

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
            boolean atingiuAlerta = item.getPrecoAlerta() > 0 ? melhorOferta.getPrecoAtual() <= item.getPrecoAlerta() : melhorOferta.getPercentualDesconto() > 0;

            if (atingiuAlerta) {
                emailService.enviarAlertaOferta(usuario.getEmail(), item.getJogo().getTitulo(), melhorOferta.getPrecoAtual());
                item.setNotificarOferta(false);
                itemListaDesejosRepository.save(item);
                System.out.println("Alerta enviado e desativado para o usuário: " + usuario.getEmail());
            }
        }
    }
}