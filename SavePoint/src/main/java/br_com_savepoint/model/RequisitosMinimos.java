package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_requisitos_minimos")
public class RequisitosMinimos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processador;
    private String memoria;
    private String placaDeVideo;
    private String sistemaOperacional;

    public RequisitosMinimos() {
    }

    // Opcional: cria a ligação de volta para a classe Jogo
    @OneToOne(mappedBy = "requisitosMinimos")
    private Jogo jogo;
}