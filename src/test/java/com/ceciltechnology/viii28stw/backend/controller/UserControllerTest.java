package com.ceciltechnology.viii28stw.backend.controller;

import com.ceciltechnology.viii28stw.backend.enumeration.Sex;
import com.ceciltechnology.viii28stw.backend.enumeration.UserAcessLevel;
import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import com.ceciltechnology.viii28stw.backend.service.IUserService;
import com.ceciltechnology.viii28stw.backend.util.RandomValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {
    @Value("${basic.auth.user}")
    private String basicAuthUser;
    @Value("${basic.auth.password}")
    private String basicAuthPassword;
    @Value("${header.name}")
    private String headerName;
    
    @Autowired
    private IUserService userService;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private ObjectMapper mapper;
    @Value("${url.prefix}")
    private String urlPrefix;
    @Value("${url.login}")
    private String urlLogin;
    @Value("${url.search.user.by.id}")
    private String urlSearchUserById;
    @Value("${url.search.all.users}")
    private String urlSearchAllUsers;
    @Value("${url.save.user}")
    private String urlSaveUser;
    @Value("${url.update.user}")
    private String urlUpdateUser;
    @Value("${url.delete.user.by.id}")
    private String urlDeleteUserById;
    private static boolean INITIALIZED = false;

    @Before
    public void CriarUsuarioAdiministradorEFazerLogin() {
        if (!INITIALIZED) {
            UserDto userDto = userService.saveUser(UserDto.builder()
                    .fullName(RandomValue.randomAlphabetic(25))
                    .nickName(RandomValue.randomAlphabetic(25))
                    .email(RandomValue.randomAlphabetic(6).concat("@")
                            .concat(RandomValue.randomAlphabetic(4)).concat(".")
                            .concat(RandomValue.randomAlphabetic(2)))
                    .userAcessLevel(UserAcessLevel.ADMINISTRATOR)
                    .password(RandomValue.randomAlphanumeric(8))
                    .sex(Sex.MALE)
                    .dateOfBirth(LocalDate.now())
                    .build());

            UserDto userDto1 = userService.fazerLogin(userDto);
            httpHeaders.add(headerName, userDto1.getEmail());
            INITIALIZED = true;
        }
    }

    @Test
    public void saveUserSenhaNaoPodeTerTamanhoMaiorQue10() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .password(RandomValue.randomAlphanumeric(11))
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof DataIntegrityViolationException);
    }

    @Test
    public void saveUserNaoPodeRetornarNuloEnaoDeixarSalvarDoisUsuariosComEmailJaExistente() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(10))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof UserDto);

        ResponseEntity responseEntityUsuario1 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario1.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario1.getBody());
        then(responseEntityUsuario1.getBody() instanceof IllegalArgumentException);
    }

    @Test
    public void updateUserNaoPodeRetornarNuloEOUsuarioASerAtualizadoDeveConterID() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(userDto1);

        ResponseEntity responseEntityLogin = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlLogin), HttpMethod.POST,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityLogin.getStatusCode()).isEqualTo(HttpStatus.OK);

        httpHeaders.add(headerName, userDto1.getEmail());

        userDto1.setFullName(RandomValue.randomAlphabetic(25));
        userDto1.setNickName(RandomValue.randomAlphabetic(25));
        userDto1.setEmail(RandomValue.randomAlphabetic(7).concat("@")
                .concat(RandomValue.randomAlphabetic(5)).concat(".")
                .concat(RandomValue.randomAlphabetic(3)));
        userDto1.setPassword(RandomValue.randomAlphanumeric(8));
        userDto1.setUserAcessLevel(UserAcessLevel.COMMON_USER);
        userDto1.setSex(Sex.FEMALE);
        userDto1.setDateOfBirth(LocalDate.now());

        ResponseEntity responseEntityUsuario1 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlUpdateUser), HttpMethod.PUT,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseEntityUsuario1.getBody());
        then(responseEntityUsuario1.getBody() instanceof UserDto);


        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseEntityUsuario2.getBody());
        then(responseEntityUsuario2.getBody() instanceof UserDto);

        UserDto userDto3 = mapper.readValue(responseEntityUsuario2.getBody().toString(), UserDto.class);

        assertEquals(userDto3, userDto1);

        userDto1.setId(null);
        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlUpdateUser), HttpMethod.PUT,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario3.getBody());
        then(responseEntityUsuario3.getBody() instanceof IllegalArgumentException);
    }

    @Test
    public void naoDeixarSalvarUsuarioSemNome() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioComNomeVazio() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName("    ")
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioSemSobrenome() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioComSobrenomeVazio() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName("    ")
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioSemEmail() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioComEmailNoFormatoErrado() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email("@" + RandomValue.randomAlphabetic(5).concat(".").concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);


        userDto.setEmail(RandomValue.randomAlphabetic(7));
        ResponseEntity responseEntityUsuario1 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario1.getBody());
        then(responseEntityUsuario1.getBody() instanceof MethodArgumentNotValidException);

        userDto.setEmail(RandomValue.randomAlphabetic(7) + "@");
        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario2.getBody());
        then(responseEntityUsuario2.getBody() instanceof MethodArgumentNotValidException);

        userDto.setEmail(RandomValue.randomAlphabetic(7).concat("@").concat(RandomValue.randomAlphabetic(5)));
        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario3.getBody());
        then(responseEntityUsuario3.getBody() instanceof IllegalArgumentException);

        userDto.setEmail(RandomValue.randomAlphabetic(7).concat("@").concat(".").concat(RandomValue.randomAlphabetic(3)));
        ResponseEntity responseEntityUsuario4 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario4.getBody());
        then(responseEntityUsuario4.getBody() instanceof MethodArgumentNotValidException);

        userDto.setEmail(RandomValue.randomAlphabetic(7).concat("@").concat(RandomValue.randomAlphabetic(5)).concat("."));
        ResponseEntity responseEntityUsuario5 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario5.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario5.getBody());
        then(responseEntityUsuario5.getBody() instanceof MethodArgumentNotValidException);

        userDto.setEmail(RandomValue.randomAlphabetic(7).concat("@")
                .concat(RandomValue.randomAlphabetic(5)).concat(".")
                .concat(RandomValue.randomAlphabetic(1)));
        ResponseEntity responseEntityUsuario6 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario6.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario6.getBody());
        then(responseEntityUsuario6.getBody() instanceof IllegalArgumentException);

        userDto.setEmail(RandomValue.randomAlphabetic(7).concat("@")
                .concat(RandomValue.randomAlphabetic(5)).concat(".")
                .concat(RandomValue.randomAlphabetic(8)));
        ResponseEntity responseEntityUsuario7 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario7.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario7.getBody());
        then(responseEntityUsuario7.getBody() instanceof IllegalArgumentException);

        userDto.setEmail(RandomValue.randomAlphabetic(7) + RandomValue.randomAlphabetic(5) + "." + RandomValue.randomAlphabetic(8));
        ResponseEntity responseEntityUsuario8 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario8.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario8.getBody());
        then(responseEntityUsuario8.getBody() instanceof MethodArgumentNotValidException);

    }

    @Test
    public void naoDeixarSalvarUsuarioSemSexo() {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat(RandomValue.randomAlphabetic(5)).concat(".").concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void searchUserByIdNaoPodeRetornarNulo() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(userDto1);

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto2 = mapper.readValue(responseEntityUsuario2.getBody().toString(), UserDto.class);

        assertNotNull(userDto2);
        assertEquals(userDto2, userDto1);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario4 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario4.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        then(responseEntityUsuario4.getBody() instanceof NoSuchElementException);
    }

    @Test
    public void searchAllUsers() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(userDto1);

        ResponseEntity<UserDto[]> responseEntityUsuarios = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchAllUsers), HttpMethod.GET,
                        new HttpEntity<>(httpHeaders), UserDto[].class);

        then(responseEntityUsuarios.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<UserDto> listuserDto = Arrays.asList(responseEntityUsuarios.getBody());

        assertNotNull(listuserDto);
        then(listuserDto.isEmpty());
        assertNotNull(listuserDto.get(0));
        then(listuserDto.get(0) instanceof UserDto);
    }

    @Test
    public void deleteUserById() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(userDto1);

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        then(responseEntityUsuario3.getBody() instanceof NoSuchElementException);
    }

    @Test
    public void deleteUserByIdInexistente() throws IOException {
        @SuppressWarnings("rawtypes")
        HttpEntity request = new HttpEntity<>(httpHeaders);

        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.COMMON_USER)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(userDto1);

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        then(responseEntityUsuario3.getBody() instanceof EmptyResultDataAccessException);
    }

    @Test
    public void fazerLogin() throws IOException {
        @SuppressWarnings("rawtypes")

        UserDto userDto = UserDto.builder()
                .fullName(RandomValue.randomAlphabetic(25))
                .nickName(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .password(RandomValue.randomAlphanumeric(8))
                .userAcessLevel(UserAcessLevel.ADMINISTRATOR)
                .sex(Sex.MALE)
                .dateOfBirth(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(userDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(userDto1);

        UserDto usDto = UserDto.builder()
                .email(userDto1.getEmail())
                .password(userDto.getPassword())
                .build();

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlLogin), HttpMethod.POST,
                        new HttpEntity<>(usDto, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDto2 = mapper.readValue(responseEntityUsuario2.getBody().toString(), UserDto.class);

        assertNotNull(userDto2);
        assertEquals(userDto2, userDto1);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(userDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario4 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlLogin), HttpMethod.POST,
                        new HttpEntity<>(usDto, httpHeaders), String.class);

        assertNull(responseEntityUsuario4.getBody());
        then(responseEntityUsuario4.getBody() instanceof EmptyResultDataAccessException);
    }

}
