package com.ceciltechnology.viii28stw.backend.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Plamedi L. Lusembo
 */

@AllArgsConstructor
@Getter
public enum DespesaEnum {
    HABITACAO(1, "Habitação"),
    SAUDE(2, "Saúde"),
    TRANSPORTE(3, "Transporte"),
    AUTOMOVEL(4, "Automóvel"),
    DESPESAS_PESSOAIS(5, "Despesas Pessoais"),
    CARTOES_CREDITO(6, "Cartões de crédito"),
    LAZER(7, "Lazer"),
    DEPENDENTES(8, "Dependentes");

    private final int codigo;
    private final String descricao;
}
