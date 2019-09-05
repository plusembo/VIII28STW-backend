package com.ceciltechnology.viii28stw.backend.service;

import com.ceciltechnology.viii28stw.backend.enumeration.Sexo;
import com.ceciltechnology.viii28stw.backend.enumeration.UsuarioNivelAcesso;
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
public class UsuarioServiceTest {

    @Autowired
    private IUsuarioService usuarioService;

    @Test(expected = IllegalArgumentException.class)
    public void salvarUsuarioNaoPodeInformarEmailInvalido() {
        UserDto usuarioDto = UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email("@".concat(randomAlphabetic(5)).concat(".").concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(10))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build();

        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7));
        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7).concat("@"));
        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7).concat("@").concat(randomAlphabetic(5)));
        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7).concat("@").concat(".").concat(randomAlphabetic(3)));
        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7).concat("@").concat(randomAlphabetic(5)).concat("."));
        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7).concat("@")
                .concat(randomAlphabetic(5)).concat(".")
                .concat(randomAlphabetic(1)));
        usuarioService.salvarUsuario(usuarioDto);

        usuarioDto.setEmail(randomAlphabetic(7).concat("@")
                .concat(randomAlphabetic(5)).concat(".")
                .concat(randomAlphabetic(8)));
        usuarioService.salvarUsuario(usuarioDto);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void salvarUsuarioSenhaNaoPodeTerTamanhoMaiorQue10() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(11))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void salvarUsuarioNaoPodeRetornarNuloEnaoDeixarSalvarDoisUsuariosComEmailJaExistente() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(10))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);
        usuarioService.salvarUsuario(usuarioDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void atualizarUsuarioNaoPodeRetornarNuloEOUsuarioASerAtualizadoDeveConterID() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        usuarioDto.setNome(randomAlphabetic(25));
        usuarioDto.setSobreNome(randomAlphabetic(25));
        usuarioDto.setEmail(randomAlphabetic(7).concat("@")
                .concat(randomAlphabetic(5)).concat(".")
                .concat(randomAlphabetic(3)));
        usuarioDto.setSenha(randomAlphanumeric(8));
        usuarioDto.setUsuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM);
        usuarioDto.setSexo(Sexo.FEMININO);
        usuarioDto.setDataNascimento(LocalDate.now());

        assertNotNull(usuarioService.atualizarUsuario(usuarioDto));

        UserDto usuarioDto1 = usuarioService.buscarUsuarioPorId(usuarioDto.getCodigo());
        assertNotNull(usuarioDto1);
        assertEquals(usuarioDto1, usuarioDto);

        usuarioDto.setCodigo(null);
        usuarioService.atualizarUsuario(usuarioDto);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemNome() {
        usuarioService.salvarUsuario(UserDto.builder()
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemSobrenome() {
        usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void naoDeixarSalvarUsuarioSemEmail() {
        usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemSenha() {
        usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void naoDeixarSalvarUsuarioSemSexo() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .dataNascimento(LocalDate.now())
                .build());

        usuarioService.salvarUsuario(usuarioDto);
    }

    @Test
    public void buscarUsuarioMaiorCodigo() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        UserDto usuarioDto1 = usuarioService.buscarUsuarioMaiorCodigo();

        assertNotNull(usuarioDto1);
        assertEquals(usuarioDto, usuarioDto1);
    }

    @Test(expected = NoSuchElementException.class)
    public void buscarUsuarioPorIdNaoPodeRetornarNulo() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        UserDto usuarioDto1 = usuarioService.buscarUsuarioPorId(usuarioDto.getCodigo());

        assertNotNull(usuarioDto1);
        assertEquals(usuarioDto, usuarioDto1);

        assertTrue(usuarioService.deletarUsuarioPorId(usuarioDto.getCodigo()));
        usuarioService.buscarUsuarioPorId(usuarioDto.getCodigo());
    }

    @Test
    public void buscarTodosOsUsuarios() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        List<UserDto> listUsuariosDto = usuarioService.buscarTodosOsUsuarios();

        assertNotNull(listUsuariosDto);
        assertFalse(listUsuariosDto.isEmpty());
        assertNotNull(listUsuariosDto.get(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void deletarUsuarioPorId() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        assertTrue(usuarioService.deletarUsuarioPorId(usuarioDto.getCodigo()));
        usuarioService.buscarUsuarioPorId(usuarioDto.getCodigo());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void deletarUsuarioPorIdInexistente() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        assertTrue(usuarioService.deletarUsuarioPorId(usuarioDto.getCodigo()));
        usuarioService.deletarUsuarioPorId(usuarioDto.getCodigo());
    }

    public void fazerLogin() {
        UserDto usuarioDto = usuarioService.salvarUsuario(UserDto.builder()
                .nome(randomAlphabetic(25))
                .sobreNome(randomAlphabetic(25))
                .email(randomAlphabetic(7).concat("@")
                        .concat(randomAlphabetic(5)).concat(".")
                        .concat(randomAlphabetic(3)))
                .senha(randomAlphanumeric(8))
                .usuarioNivelAcesso(UsuarioNivelAcesso.USUARIO_COMUM)
                .sexo(Sexo.MASCULINO)
                .dataNascimento(LocalDate.now())
                .build());

        assertNotNull(usuarioDto);

        UserDto usuarioDto1 = usuarioService.fazerLogin(usuarioDto);

        assertNotNull(usuarioDto1);
        assertEquals(usuarioDto1, usuarioDto);

        assertTrue(usuarioService.deletarUsuarioPorId(usuarioDto.getCodigo()));
        assertNull(usuarioService.fazerLogin(usuarioDto));
    }

}
