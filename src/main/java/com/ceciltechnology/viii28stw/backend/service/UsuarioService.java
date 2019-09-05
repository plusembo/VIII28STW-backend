package com.ceciltechnology.viii28stw.backend.service;

import com.ceciltechnology.viii28stw.backend.model.dto.UserDto;
import com.ceciltechnology.viii28stw.backend.model.entity.User;
import com.ceciltechnology.viii28stw.backend.repository.IUsuarioRepository;
import com.ceciltechnology.viii28stw.backend.util.EmailValidator;
import com.ceciltechnology.viii28stw.backend.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("usuarioService")
public class UsuarioService implements IUsuarioService {

    @Autowired private IUsuarioRepository usuarioRepository;
    private final Set<String> userLoggedIn = new HashSet();

    public boolean isUserLoggedIn(String email) {
        return userLoggedIn.stream()
                .filter(us -> us.equals(email)).findFirst().orElse(null) != null;
    }

    public UserDto buscarUsuarioMaiorCodigo() {
        User usuario = usuarioRepository.findFirstByOrderByCodigoDesc();
        if(usuario != null){
            return UserDto.builder()
                    .codigo(usuario.getCodigo())
                    .nome(usuario.getNome())
                    .sobreNome(usuario.getSobreNome())
                    .email(usuario.getEmail())
                    .usuarioNivelAcesso(usuario.getUsuarioNivelAcesso())
                    .senha(usuario.getSenha())
                    .sexo(usuario.getSexo())
                    .dataNascimento(usuario.getDataNascimento())
                    .build();
        } else return null;
    }

    @Override
    public UserDto buscarUsuarioPorId(String id) {
        Optional<User> usuarioOptional = usuarioRepository.findById(id);

        if(usuarioOptional.isPresent()){
            User usuario = usuarioOptional.get();
            return UserDto.builder()
                    .codigo(usuario.getCodigo())
                    .nome(usuario.getNome())
                    .sobreNome(usuario.getSobreNome())
                    .email(usuario.getEmail())
                    .usuarioNivelAcesso(usuario.getUsuarioNivelAcesso())
                    .senha(usuario.getSenha())
                    .sexo(usuario.getSexo())
                    .dataNascimento(usuario.getDataNascimento())
                    .build();

        } else throw new NoSuchElementException("Não existe usuário com o ID informado");
    }

    @Override
    public List<UserDto> buscarTodosOsUsuarios(){
        List<UserDto> usuariosDto = new ArrayList();
        for(User usuario : usuarioRepository.findAll()) {
            usuariosDto.add(UserDto.builder()
                    .codigo(usuario.getCodigo())
                    .nome(usuario.getNome())
                    .sobreNome(usuario.getSobreNome())
                    .email(usuario.getEmail())
                    .usuarioNivelAcesso(usuario.getUsuarioNivelAcesso())
                    .senha(usuario.getSenha())
                    .sexo(usuario.getSexo())
                    .dataNascimento(usuario.getDataNascimento())
                    .build());
        }
        return usuariosDto;
    }

    @Override
    public UserDto salvarUsuario(UserDto usuarioDto) {
        if (!EmailValidator.isValidEmail(usuarioDto.getEmail())) {
                throw new IllegalArgumentException("Este e-mail não é válido");
        }else if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
                throw new IllegalArgumentException("Este e-mail já existe");
        }

        usuarioDto.setCodigo(buscarUsuarioMaiorCodigo() == null ?
                IdGenerator.getInstance().generate() :
                buscarUsuarioMaiorCodigo().getCodigo());

        return persistir(usuarioDto);
    }

    @Override
    public UserDto atualizarUsuario(UserDto usuarioDto) {
        if (usuarioDto.getCodigo() == null || usuarioDto.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("O usuário informado não contem ID");
        }
        return persistir(usuarioDto);
    }

    private UserDto persistir(UserDto usuarioDto) {
        User usuario = usuarioRepository.save(User.builder()
                .codigo(usuarioDto.getCodigo())
                .nome(usuarioDto.getNome())
                .sobreNome(usuarioDto.getSobreNome())
                .email(usuarioDto.getEmail())
                .usuarioNivelAcesso(usuarioDto.getUsuarioNivelAcesso())
                .senha(usuarioDto.getSenha())
                .sexo(usuarioDto.getSexo())
                .dataNascimento(usuarioDto.getDataNascimento())
                .build());

        return UserDto.builder()
                .codigo(usuario.getCodigo())
                .nome(usuario.getNome())
                .sobreNome(usuario.getSobreNome())
                .email(usuario.getEmail())
                .usuarioNivelAcesso(usuario.getUsuarioNivelAcesso())
                .senha(usuario.getSenha())
                .sexo(usuario.getSexo())
                .dataNascimento(usuario.getDataNascimento())
                .build();
    }

    @Override
    public boolean deletarUsuarioPorId(String id){
        usuarioRepository.deleteById(id);
        return true;
    }

    @Override
    public UserDto fazerLogin(UserDto usuarioDto){
        User usuario = usuarioRepository.findByEmailAndSenha(usuarioDto.getEmail(), usuarioDto.getSenha());
        if (usuario == null) return null;
        userLoggedIn.add(usuario.getEmail());

            return UserDto.builder()
                .codigo(usuario.getCodigo())
                .nome(usuario.getNome())
                .sobreNome(usuario.getSobreNome())
                .email(usuario.getEmail())
                .usuarioNivelAcesso(usuario.getUsuarioNivelAcesso())
                .senha(usuario.getSenha())
                .sexo(usuario.getSexo())
                .dataNascimento(usuario.getDataNascimento())
                .build();
    }

    @Override
    public void sair(String email){
        if(userLoggedIn.remove(email)) {
            throw new NoSuchElementException("usuário não está logado");
        }
    }

}
