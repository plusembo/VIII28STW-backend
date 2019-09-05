package com.ceciltechnology.viii28stw.backend.model.entity;

import com.ceciltechnology.viii28stw.backend.enumeration.Sex;
import com.ceciltechnology.viii28stw.backend.enumeration.UserAcessLevel;
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
@Table(name="user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 25)
    private String id;

    @Column(name = "full_name", length = 25, nullable = false)
    private String fullName;

    @Column(name = "nick_name", length = 25, nullable = false)
    private String nickName;

    @Email
    @Column(name = "email", length = 25, nullable = false)
    private String email;

    @Column(name = "password", length = 10, nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_acess_level", nullable = false)
    private UserAcessLevel userAcessLevel;

}
