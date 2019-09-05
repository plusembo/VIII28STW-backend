package com.ceciltechnology.viii28stw.backend.service;


import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;

import java.util.List;

public interface IUsuarioService {

    UserDto buscarUsuarioMaiorCodigo();

    boolean isUserLoggedIn(String email);

    UserDto buscarUsuarioPorId(String id);

    List<UserDto> buscarTodosOsUsuarios();

    UserDto salvarUsuario(UserDto usuarioDto);

    UserDto atualizarUsuario(UserDto usuarioDto);

    boolean deletarUsuarioPorId(String id);

    UserDto fazerLogin(UserDto usuarioDto);

    void sair(String email);

}
