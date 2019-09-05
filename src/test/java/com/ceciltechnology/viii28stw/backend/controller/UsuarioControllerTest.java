package com.ceciltechnology.viii28stw.backend.controller;

import com.ceciltechnology.viii28stw.backend.enumeration.Sexo;
import com.ceciltechnology.viii28stw.backend.enumeration.UsuarioNivelAcesso;
import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import com.ceciltechnology.viii28stw.backend.service.IUsuarioService;
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
public class UsuarioControllerTest {
    @Value("${basic.auth.user}")
    private String basicAuthUser;
    @Value("${basic.auth.password}")
    private String basicAuthPassword;
    @Value("${header.name}")
    private String headerName;
    
    @Autowired
    private IUsuarioService usuarioService;
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
            UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                    .nome(RandomValue.randomAlphabetic(25))
                    .sobreNome(RandomValue.randomAlphabetic(25))
                    .email(RandomValue.randomAlphabetic(6).concat("@")
                            .concat(RandomValue.randomAlphabetic(4)).concat(".")
                            .concat(RandomValue.randomAlphabetic(2)))
                    .usuarioNivelAcesso(UsuarioNivelAcesso.ADMINISTRADOR)
                    .senha(RandomValue.randomAlphanumeric(8))
                    .sexo(Sexo.MASCULINO)
                    .dataNascimento(LocalDate.now())
                    .build());

            UserDto usuarioDto1 = usuarioService.fazerLogin(usuarioDto);
            httpHeaders.add(headerName, usuarioDto1.getEmail());
            INITIALIZED = true;
        }
    }

    @Test
    public void salvarUsuarioSenhaNaoPodeTerTamanhoMaiorQue10() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .senha(RandomValue.randomAlphanumeric(11))
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof DataIntegrityViolationException);
    }

    @Test
    public void salvarUsuarioNaoPodeRetornarNuloEnaoDeixarSalvarDoisUsuariosComEmailJaExistente() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(10))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof UserDto);

        ResponseEntity responseEntityUsuario1 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario1.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario1.getBody());
        then(responseEntityUsuario1.getBody() instanceof IllegalArgumentException);
    }

    @Test
    public void atualizarUsuarioNaoPodeRetornarNuloEOUsuarioASerAtualizadoDeveConterID() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto1);

        ResponseEntity responseEntityLogin = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlLogin), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityLogin.getStatusCode()).isEqualTo(HttpStatus.OK);

        httpHeaders.add(headerName, usuarioDto1.getEmail());

        usuarioDto1.setNome(RandomValue.randomAlphabetic(25));
        usuarioDto1.setSobreNome(RandomValue.randomAlphabetic(25));
        usuarioDto1.setEmail(RandomValue.randomAlphabetic(7).concat("@")
                .concat(RandomValue.randomAlphabetic(5)).concat(".")
                .concat(RandomValue.randomAlphabetic(3)));
        usuarioDto1.setSenha(RandomValue.randomAlphanumeric(8));
        usuarioDto1.setUsuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM);
        usuarioDto1.setSexo(Sexo.FEMININO);
        usuarioDto1.setDataNascimento(LocalDate.now());

        ResponseEntity responseEntityUsuario1 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlUpdateUser), HttpMethod.PUT,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseEntityUsuario1.getBody());
        then(responseEntityUsuario1.getBody() instanceof UserDto);


        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseEntityUsuario2.getBody());
        then(responseEntityUsuario2.getBody() instanceof UserDto);

        UserDto usuarioDto3 = mapper.readValue(responseEntityUsuario2.getBody().toString(), UserDto.class);

        assertEquals(usuarioDto3, usuarioDto1);

        usuarioDto1.setCodigo(null);
        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlUpdateUser), HttpMethod.PUT,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario3.getBody());
        then(responseEntityUsuario3.getBody() instanceof IllegalArgumentException);
    }

    @Test
    public void naoDeixarSalvarUsuarioSemNome() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioComNomeVazio() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome("    ")
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioSemSobrenome() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioComSobrenomeVazio() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome("    ")
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioSemEmail() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void naoDeixarSalvarUsuarioComEmailNoFormatoErrado() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email("@" + RandomValue.randomAlphabetic(5).concat(".").concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);


        usuarioDto.setEmail(RandomValue.randomAlphabetic(7));
        ResponseEntity responseEntityUsuario1 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario1.getBody());
        then(responseEntityUsuario1.getBody() instanceof MethodArgumentNotValidException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7) + "@");
        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario2.getBody());
        then(responseEntityUsuario2.getBody() instanceof MethodArgumentNotValidException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7).concat("@").concat(RandomValue.randomAlphabetic(5)));
        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario3.getBody());
        then(responseEntityUsuario3.getBody() instanceof IllegalArgumentException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7).concat("@").concat(".").concat(RandomValue.randomAlphabetic(3)));
        ResponseEntity responseEntityUsuario4 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario4.getBody());
        then(responseEntityUsuario4.getBody() instanceof MethodArgumentNotValidException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7).concat("@").concat(RandomValue.randomAlphabetic(5)).concat("."));
        ResponseEntity responseEntityUsuario5 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario5.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario5.getBody());
        then(responseEntityUsuario5.getBody() instanceof MethodArgumentNotValidException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7).concat("@")
                .concat(RandomValue.randomAlphabetic(5)).concat(".")
                .concat(RandomValue.randomAlphabetic(1)));
        ResponseEntity responseEntityUsuario6 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario6.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario6.getBody());
        then(responseEntityUsuario6.getBody() instanceof IllegalArgumentException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7).concat("@")
                .concat(RandomValue.randomAlphabetic(5)).concat(".")
                .concat(RandomValue.randomAlphabetic(8)));
        ResponseEntity responseEntityUsuario7 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario7.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(responseEntityUsuario7.getBody());
        then(responseEntityUsuario7.getBody() instanceof IllegalArgumentException);

        usuarioDto.setEmail(RandomValue.randomAlphabetic(7) + RandomValue.randomAlphabetic(5) + "." + RandomValue.randomAlphabetic(8));
        ResponseEntity responseEntityUsuario8 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario8.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario8.getBody());
        then(responseEntityUsuario8.getBody() instanceof MethodArgumentNotValidException);

    }

    @Test
    public void naoDeixarSalvarUsuarioSemSexo() {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat(RandomValue.randomAlphabetic(5)).concat(".").concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntityUsuario.getBody());
        then(responseEntityUsuario.getBody() instanceof MethodArgumentNotValidException);
    }

    @Test
    public void buscarUsuarioPorIdNaoPodeRetornarNulo() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto1);

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto2 = mapper.readValue(responseEntityUsuario2.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto2);
        assertEquals(usuarioDto2, usuarioDto1);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario4 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario4.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        then(responseEntityUsuario4.getBody() instanceof NoSuchElementException);
    }

    @Test
    public void buscarTodosOsUsuarios() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto1);

        ResponseEntity<UserDto[]> responseEntityUsuarios = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchAllUsers), HttpMethod.GET,
                        new HttpEntity<>(httpHeaders), UserDto[].class);

        then(responseEntityUsuarios.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<UserDto> listusuarioDto = Arrays.asList(responseEntityUsuarios.getBody());

        assertNotNull(listusuarioDto);
        then(listusuarioDto.isEmpty());
        assertNotNull(listusuarioDto.get(0));
        then(listusuarioDto.get(0) instanceof UserDto);
    }

    @Test
    public void deletarUsuarioPorId() throws IOException {
        @SuppressWarnings("rawtypes")
        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto1);

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSearchUserById), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        then(responseEntityUsuario3.getBody() instanceof NoSuchElementException);
    }

    @Test
    public void deletarUsuarioPorIdInexistente() throws IOException {
        @SuppressWarnings("rawtypes")
        HttpEntity request = new HttpEntity<>(httpHeaders);

        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto1);

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        then(responseEntityUsuario3.getBody() instanceof EmptyResultDataAccessException);
    }

    @Test
    public void fazerLogin() throws IOException {
        @SuppressWarnings("rawtypes")

        UserDto usuarioDto = UserDto.builder()
                .nome(RandomValue.randomAlphabetic(25))
                .sobreNome(RandomValue.randomAlphabetic(25))
                .email(RandomValue.randomAlphabetic(7).concat("@")
                        .concat(RandomValue.randomAlphabetic(5)).concat(".")
                        .concat(RandomValue.randomAlphabetic(3)))
                .senha(RandomValue.randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.ADMINISTRADOR)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        ResponseEntity responseEntityUsuario = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlSaveUser), HttpMethod.POST,
                        new HttpEntity<>(usuarioDto, httpHeaders), String.class);

        then(responseEntityUsuario.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto1 = mapper.readValue(responseEntityUsuario.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto1);

        UserDto usDto = UserDto.builder()
                .email(usuarioDto1.getEmail())
                .senha(usuarioDto.getSenha())
                .build();

        ResponseEntity responseEntityUsuario2 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlLogin), HttpMethod.POST,
                        new HttpEntity<>(usDto, httpHeaders), String.class);

        then(responseEntityUsuario2.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto usuarioDto2 = mapper.readValue(responseEntityUsuario2.getBody().toString(), UserDto.class);

        assertNotNull(usuarioDto2);
        assertEquals(usuarioDto2, usuarioDto1);

        ResponseEntity responseEntityUsuario3 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlDeleteUserById), HttpMethod.DELETE,
                        new HttpEntity<>(usuarioDto1, httpHeaders), String.class);

        then(responseEntityUsuario3.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity responseEntityUsuario4 = testRestTemplate
                .withBasicAuth(basicAuthUser, basicAuthPassword)
                .exchange(urlPrefix.concat(urlLogin), HttpMethod.POST,
                        new HttpEntity<>(usDto, httpHeaders), String.class);

        assertNull(responseEntityUsuario4.getBody());
        then(responseEntityUsuario4.getBody() instanceof EmptyResultDataAccessException);
    }

}
