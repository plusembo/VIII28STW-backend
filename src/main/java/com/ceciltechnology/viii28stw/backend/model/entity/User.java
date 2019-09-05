package com.ceciltechnology.viii28stw.backend.model.entity;

import com.ceciltechnology.viii28stw.backend.enumeration.Sexo;
import com.ceciltechnology.viii28stw.backend.enumeration.UsuarioNivelAcesso;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", length = 25)
    private String codigo;

    @Column(name = "NOME", length = 25, nullable = false)
    private String nome;

    @Column(name = "SOBRE_NOME", length = 25, nullable = false)
    private String sobreNome;

    @Email
    @Column(name = "EMAIL", length = 25, nullable = false)
    private String email;

    @Column(name = "SENHA", length = 10, nullable = false)
    private String senha;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "SEXO", nullable = false)
    private Sexo sexo;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "NIVEL_ACESSO", nullable = false)
    private UsuarioNivelAcesso usuarioNivelAcesso;

}
