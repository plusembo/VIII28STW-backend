package com.ceciltechnology.viii28stw.backend.controller;

import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import com.ceciltechnology.viii28stw.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/viii28stw")
public class UserController {

    private final static String USER_LOGGED_IN = "user_logged_in";

    @Autowired
    private IUserService userService;

    @PostMapping("/searchuserbyid")
    public ResponseEntity<UserDto> searchUserById(@RequestHeader HashMap<String, Object> headers,
                                                      @RequestBody @Valid UserDto userIdDto){
        if(!userService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        UserDto userDto = userService.searchUserById(userIdDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/searchallusers")
    public ResponseEntity<List<UserDto>> searchAllUsers(@RequestHeader HashMap<String, Object> headers) {
        if(!userService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        List<UserDto> usersDto = userService.searchAllUsers();
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    @PostMapping("/saveuser")
    public ResponseEntity<UserDto> saveUser(@RequestHeader HashMap<String, Object> headers,
                                                 @RequestBody @Valid UserDto userDto) {
        if(!userService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userService.saveUser(userDto), HttpStatus.OK);
    }

    @PutMapping("/updateuser")
    public ResponseEntity<UserDto> updateUser(@RequestHeader HashMap<String, Object> headers,
                                                    @RequestBody @Valid UserDto userDto) {
        if(!userService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
    }

    @DeleteMapping("/deleteuserbyid")
    public ResponseEntity<Boolean> deleteUserById(@RequestHeader HashMap<String, Object> headers,
                                                       @RequestBody @Valid UserDto userDto) {
        if(!userService.isUserLoggedIn(String.valueOf(headers.get(USER_LOGGED_IN)))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userService.deleteUserById(userDto.getId()), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.fazerLogin(userDto), HttpStatus.OK);
    }

}
