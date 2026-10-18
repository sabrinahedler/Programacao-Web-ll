package br_com_savepoint.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAlertaOferta(String destinatario, String nomeJogo, Double precoAtual) {
        if (destinatario == null || !destinatario.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("E-mail ignorado: formato inválido -> " + destinatario);
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom("alerta@savepoint.com");
            mensagem.setTo(destinatario);
            mensagem.setSubject("🔥 Oferta SavePoint: " + nomeJogo + " em promoção!");
            mensagem.setText("Olá!\n\nO jogo " + nomeJogo + " da sua Lista de Desejos entrou em promoção por apenas R$ " + precoAtual + "!\n\nAproveite no SavePoint e compre antes que a promoção acabe, patrão!");
            mailSender.send(mensagem);

        } catch (Exception e) {
            System.err.println(">>> Falha no disparo para " + destinatario + ": " + e.getMessage());
        }
    }
}