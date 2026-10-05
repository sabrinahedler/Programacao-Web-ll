package br_com_savepoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Requisitos mínimos de hardware e sistema para executar um jogo. */
@Getter
@Setter
@NoArgsConstructor
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

    @JsonIgnore
    @OneToOne(mappedBy = "requisitosMinimos")
    private Jogo jogo;
}
