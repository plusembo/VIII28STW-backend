package com.ceciltechnology.viii28stw.backend.model.dto;

import com.ceciltechnology.viii28stw.backend.enumeration.Sex;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ceciltechnology.viii28stw.backend.enumeration.UserAcessLevel;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class UserDto {

    private String id;
    @NotBlank private String fullName;
    @NotBlank private String nickName;
    @NotBlank @Email private String email;
    @NotBlank private String password;
    private Sex sex;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;

    private UserAcessLevel userAcessLevel;

}
