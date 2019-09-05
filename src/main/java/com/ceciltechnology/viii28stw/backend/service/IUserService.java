package com.ceciltechnology.viii28stw.backend.service;


import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;

import java.util.List;

public interface IUserService {

    UserDto buscarUsuarioMaiorCodigo();

    boolean isUserLoggedIn(String email);

    UserDto searchUserById(String id);

    List<UserDto> searchAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    boolean deleteUserById(String id);

    UserDto fazerLogin(UserDto userDto);

    void sair(String email);

}
