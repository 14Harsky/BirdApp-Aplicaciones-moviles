package com.example.usuarios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.usuarios.dto.Usuario;
import com.example.usuarios.dto.UpdateProfileRequestDto;
import com.example.usuarios.dto.UserDetailsDto;
import com.example.usuarios.dto.ChangePasswordRequestDto;
import com.example.usuarios.dto.RegisterRequestDto;
import com.example.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> getAllUsuarios(){
        return repository.findAll();
    }

    public Usuario saveUsuario(Usuario u){
        return repository.save(u);
    }

    public Usuario login(String correo, String password){
        Usuario u = repository.findByCorreo(correo);
        if(u != null && u.getPassword() != null && u.getPassword().equals(password)){
            return u;
        }
        return null;
    }

    public UserDetailsDto updateProfile(int id, int idFromToken, UpdateProfileRequestDto dto) {
        if (id != idFromToken) {
            return null;
        }
        Usuario usuario = repository.findById(id).orElse(null);
        if (usuario == null) {
            return null;
        }
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        repository.save(usuario);
        UserDetailsDto details = new UserDetailsDto();
        details.setId(usuario.getId());
        details.setNombre(usuario.getNombre());
        details.setCorreo(usuario.getCorreo());
        return details;
    }

    public void deleteUsuario(int id) {
        repository.deleteById(id);
    }

    public void cambiarPassword(int id, String nuevaPassword) {
        Usuario usuario = repository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setPassword(nuevaPassword);
            repository.save(usuario);
        }
    }

    public boolean cambiarPasswordSeguro(int id, int idFromToken, ChangePasswordRequestDto dto) {
        if (id != idFromToken) {
            return false;
        }
        Usuario usuario = repository.findById(id).orElse(null);
        if (usuario == null) {
            return false;
        }
        // Verifica clave actual
        if (!usuario.getPassword().equals(dto.getClaveActual())) {
            return false;
        }
        // Hashea la nueva clave (simple, solo ejemplo)
        usuario.setPassword(dto.getClaveNueva());
        repository.save(usuario);
        return true;
    }

    public UserDetailsDto registerUser(RegisterRequestDto dto) {
        if (repository.findByCorreo(dto.getCorreo()) != null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword());
        Usuario saved = repository.save(usuario);
        UserDetailsDto details = new UserDetailsDto();
        details.setId(saved.getId());
        details.setNombre(saved.getNombre());
        details.setCorreo(saved.getCorreo());
        return details;
    }

    public boolean existsByCorreo(String correo) {
        return repository.findByCorreo(correo) != null;
    }

}
