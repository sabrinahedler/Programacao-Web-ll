package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Registro de um preço assumido por uma oferta em determinada data. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_historico")
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;
    private double preco;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private OfertaJogo oferta;
}
