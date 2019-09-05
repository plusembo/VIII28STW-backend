package com.ceciltechnology.viii28stw.backend.controller;

import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import com.ceciltechnology.viii28stw.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/pensiltik")
public class UsuarioController {

    private final static String USER_LOGGED_IN = "user_logged_in";

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/buscarusuarioporid")
    public ResponseEntity<UserDto> buscarUsuarioPorId(@RequestHeader HashMap<String, Object> headers,
                                                      @RequestBody @Valid UserDto usuarioIdDto){
        if(!usuarioService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        UserDto usuarioDto = usuarioService.buscarUsuarioPorId(usuarioIdDto.getCodigo());
        return new ResponseEntity<>(usuarioDto, HttpStatus.OK);
    }

    @GetMapping("/buscartodososusuarios")
    public ResponseEntity<List<UserDto>> buscarTodosOsUsuarios(@RequestHeader HashMap<String, Object> headers) {
        if(!usuarioService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        List<UserDto> usuariosDto = usuarioService.buscarTodosOsUsuarios();
        return new ResponseEntity<>(usuariosDto, HttpStatus.OK);
    }

    @PostMapping("/salvarusuario")
    public ResponseEntity<UserDto> salvarUsuario(@RequestHeader HashMap<String, Object> headers,
                                                 @RequestBody @Valid UserDto usuarioDto) {
        if(!usuarioService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(usuarioService.salvarUsuario(usuarioDto), HttpStatus.OK);
    }

    @PutMapping("/atualizarusuario")
    public ResponseEntity<UserDto> atualizarUsuario(@RequestHeader HashMap<String, Object> headers,
                                                    @RequestBody @Valid UserDto usuarioDto) {
        if(!usuarioService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(usuarioService.atualizarUsuario(usuarioDto), HttpStatus.OK);
    }

    @DeleteMapping("/deletarusuarioporid")
    public ResponseEntity<Boolean> deletarUsuarioPorId(@RequestHeader HashMap<String, Object> headers,
                                                       @RequestBody @Valid UserDto usuarioDto) {
        if(!usuarioService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(usuarioService.deletarUsuarioPorId(usuarioDto.getCodigo()), HttpStatus.OK);
    }

    @PostMapping("/fazerlogin")
    public ResponseEntity<UserDto> login(@RequestBody UserDto usuarioDto) {
        return new ResponseEntity<>(usuarioService.fazerLogin(usuarioDto), HttpStatus.OK);
    }

}
