package com.ceciltechnology.viii28stw.backend.service;

import com.ceciltechnology.viii28stw.backend.enumeration.Sex;
import com.ceciltechnology.viii28stw.backend.enumeration.UserAcessLevel;
import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static com.ceciltechnology.viii28stw.backend.util.RandomValue.randomAlphabetic;
import static com.ceciltechnology.viii28stw.backend.util.RandomValue.randomAlphanumeric;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test(expected = IllegalArgumentException.class)
    public void saveUserNaoPodeInformarEmailInvalido() {
        UserDto userDto = UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email("@".concat(randomAlphabetic(5)).concat(".").concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(10))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7));
        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7).concat("@"));
        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7).concat("@").concat(randomAlphabetic(5)));
        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7).concat("@").concat(".").concat(randomAlphabetic(3)));
        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7).concat("@").concat(randomAlphabetic(5)).concat("."));
        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7).concat("@")
                .concat(randomAlphabetic(5)).concat(".")
                .concat(randomAlphabetic(1)));
        userService.saveUser(userDto);

        userDto.setEmail(randomAlphabetic(7).concat("@")
                .concat(randomAlphabetic(5)).concat(".")
                .concat(randomAlphabetic(8)));
        userService.saveUser(userDto);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void saveUserSenhaNaoPodeTerTamanhoMaiorQue10() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(11))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveUserNaoPodeRetornarNuloEnaoDeixarSalvarDoisUsuariosComEmailJaExistente() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(10))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);
        userService.saveUser(userDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserNaoPodeRetornarNuloEOUsuarioASerAtualizadoDeveConterID() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        userDto.setFullName(randomAlphabetic(25));
        userDto.setNickName(randomAlphabetic(25));
        userDto.setEmail(randomAlphabetic(7).concat("@")
                .concat(randomAlphabetic(5)).concat(".")
                .concat(randomAlphabetic(3)));
        userDto.setPassword(randomAlphanumeric(8));
        userDto.setUserAcessLevel(UserAcessLevel.COMMON_USER);
        userDto.setSex(Sex.FEMALE);
        userDto.setDateOfBirth(LocalDate.now());

        assertNotNull(userService.updateUser(userDto));

        UserDto userDto1 = userService.searchUserById(userDto.getId());
        assertNotNull(userDto1);
        assertEquals(userDto1, userDto);

        userDto.setId(null);
        userService.updateUser(userDto);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemNome() {
        userService.saveUser(UserDto.builder()
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemSobrenome() {
        userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void naoDeixarSalvarUsuarioSemEmail() {
        userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemSenha() {
        userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemSexo() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .dateOfBirth(LocalDate.now())
                .build());

        userService.saveUser(userDto);
    }

    @Test
    public void buscarUsuarioMaiorCodigo() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        UserDto userDto1 = userService.buscarUsuarioMaiorCodigo();

        assertNotNull(userDto1);
        assertEquals(userDto, userDto1);
    }

    @Test(expected = NoSuchElementException.class)
    public void searchUserByIdNaoPodeRetornarNulo() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        UserDto userDto1 = userService.searchUserById(userDto.getId());

        assertNotNull(userDto1);
        assertEquals(userDto, userDto1);

        assertTrue(userService.deleteUserById(userDto.getId()));
        userService.searchUserById(userDto.getId());
    }

    @Test
    public void searchAllUsers() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        List<UserDto> listUsuariosDto = userService.searchAllUsers();

        assertNotNull(listUsuariosDto);
        assertFalse(listUsuariosDto.isEmpty());
        assertNotNull(listUsuariosDto.get(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteUserById() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        assertTrue(userService.deleteUserById(userDto.getId()));
        userService.searchUserById(userDto.getId());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void deleteUserByIdInexistente() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        assertTrue(userService.deleteUserById(userDto.getId()));
        userService.deleteUserById(userDto.getId());
    }

    public void fazerLogin() {
        UserDto userDto = userService.saveUser(UserDto.builder()
                .fullName(randomAlphabetic(25))
                .nickName(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .password(randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build());

        assertNotNull(userDto);

        UserDto userDto1 = userService.fazerLogin(userDto);

        assertNotNull(userDto1);
        assertEquals(userDto1, userDto);

        assertTrue(userService.deleteUserById(userDto.getId()));
        assertNull(userService.fazerLogin(userDto));
    }

}
