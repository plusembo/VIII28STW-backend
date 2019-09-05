package com.ceciltechnology.viii28stw.backend.service;

import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import com.ceciltechnology.viii28stw.backend.model.entity.User;
import com.ceciltechnology.viii28stw.backend.repository.IUserRepository;
import com.ceciltechnology.viii28stw.backend.util.EmailValidator;
import com.ceciltechnology.viii28stw.backend.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class UserService implements IUserService {

    @Autowired private IUserRepository userRepository;
    private final Set<String> userLoggedIn = new HashSet();

    public boolean isUserLoggedIn(String email) {
        return userLoggedIn.stream()
                .filter(us -> us.equals(email)).findFirst().orElse(null) != null;
    }

    public UserDto buscarUsuarioMaiorCodigo() {
        User user = userRepository.findFirstByOrderByIdDesc();
        if(user != null){
            return UserDto.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .userAcessLevel(user.getUserAcessLevel())
                    .password(user.getPassword())
                    .sex(user.getSex())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
        } else return null;
    }

    @Override
    public UserDto searchUserById(String id) {
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            return UserDto.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .userAcessLevel(user.getUserAcessLevel())
                    .password(user.getPassword())
                    .sex(user.getSex())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();

        } else throw new NoSuchElementException("Não existe usuário com o ID informado");
    }

    @Override
    public List<UserDto> searchAllUsers(){
        List<UserDto> usersDto = new ArrayList();
        for(User user : userRepository.findAll()) {
            usersDto.add(UserDto.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .userAcessLevel(user.getUserAcessLevel())
                    .password(user.getPassword())
                    .sex(user.getSex())
                    .dateOfBirth(user.getDateOfBirth())
                    .build());
        }
        return usersDto;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (!EmailValidator.isValidEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Este e-mail não é válido");
        }else if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Este e-mail já existe");
        }

        userDto.setId(buscarUsuarioMaiorCodigo() == null ?
                IdGenerator.getInstance().generate() :
                buscarUsuarioMaiorCodigo().getId());

        return persistir(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (userDto.getId() == null || userDto.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("O usuário informado não contem ID");
        }
        return persistir(userDto);
    }

    private UserDto persistir(UserDto userDto) {
        User user = userRepository.save(User.builder()
                .id(userDto.getId())
                .fullName(userDto.getFullName())
                .nickName(userDto.getNickName())
                .email(userDto.getEmail())
                .userAcessLevel(userDto.getUserAcessLevel())
                .password(userDto.getPassword())
                .sex(userDto.getSex())
                .dateOfBirth(userDto.getDateOfBirth())
                .build());

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .userAcessLevel(user.getUserAcessLevel())
                .password(user.getPassword())
                .sex(user.getSex())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }

    @Override
    public boolean deleteUserById(String id){
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public UserDto fazerLogin(UserDto userDto){
        User user = userRepository.findByEmailAndPassword(userDto.getEmail(), userDto.getPassword());
        if (user == null) return null;
        userLoggedIn.add(user.getEmail());

            return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .userAcessLevel(user.getUserAcessLevel())
                .password(user.getPassword())
                .sex(user.getSex())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }

    @Override
    public void sair(String email){
        if(userLoggedIn.remove(email)) {
            throw new NoSuchElementException("usuário não está logado");
        }
    }

}
