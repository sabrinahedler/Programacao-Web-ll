package br_com_savepoint.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "tb_historico")
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;
    private double preco;

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private OfertaJogo oferta;
}