package com.ceciltechnology.viii28stw.backend.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Plamedi L. Lusembo
 */

@AllArgsConstructor
@Getter
public enum UsuarioNivelAcesso {
    ADMINISTRADOR(1,"Admin", "Administrador"),
    USUARIO_COMUM(2,"Uscm", "Usuário comum");

    private final int id;
    private final String abreviacao;
    private final String descricao;
}
