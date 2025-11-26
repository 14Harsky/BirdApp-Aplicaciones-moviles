package com.example.usuarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.usuarios.dto.Usuario;
import com.example.usuarios.service.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.usuarios.dto.UpdateProfileRequestDto;
import com.example.usuarios.dto.UserDetailsDto;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.example.usuarios.dto.ChangePasswordRequestDto;
import com.example.usuarios.dto.RegisterRequestDto;
import com.example.usuarios.dto.LoginRequestDto;
import com.example.usuarios.dto.AuthResponseDto;

@RestController
@RequestMapping("/Api/usuario")
public class UsuarioController {
    
    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> getallUsuarios(){
        return service.getAllUsuarios();
    }

//Registrar ususario:

    @PostMapping("/register")
    public ResponseEntity<UserDetailsDto> registerUsuario(@RequestBody RegisterRequestDto dto) {
        if (service.existsByCorreo(dto.getCorreo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Usuario nuevo = new Usuario();
        nuevo.setNombre(dto.getNombre());
        nuevo.setCorreo(dto.getCorreo());
        nuevo.setPassword(dto.getPassword());
        Usuario creado = service.saveUsuario(nuevo);
        UserDetailsDto response = new UserDetailsDto();
        response.setId(creado.getId());
        response.setNombre(creado.getNombre());
        response.setCorreo(creado.getCorreo());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//Login de usuario:

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUsuario(@RequestBody LoginRequestDto dto) {
        Usuario found = service.login(dto.getCorreo(), dto.getPassword());
        if (found == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Simulación de token JWT (solo para ejemplo)
        String token = "Bearer " + found.getId();
        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        response.setId(found.getId());
        response.setNombre(found.getNombre());
        response.setCorreo(found.getCorreo());
        return ResponseEntity.ok(response);
    }


    
    @PutMapping("/editar/{id}")
    public ResponseEntity<UserDetailsDto> updateProfile(
            @PathVariable int id,
            @RequestBody UpdateProfileRequestDto dto,
            @RequestHeader("Authorization") String authHeader) {
        // Simulación de extracción de id del token JWT
        int idFromToken = extractUserIdFromJwt(authHeader);
        UserDetailsDto updated = service.updateProfile(id, idFromToken, dto);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/editar/{id}/password")
    public ResponseEntity<Void> cambiarPasswordSeguro(
            @PathVariable int id,
            @RequestBody ChangePasswordRequestDto dto,
            @RequestHeader("Authorization") String authHeader) {
        int idFromToken = extractUserIdFromJwt(authHeader);
        boolean ok = service.cambiarPasswordSeguro(id, idFromToken, dto);
        if (!ok) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader) {
        int idFromToken = extractUserIdFromJwt(authHeader);
        if (id != idFromToken) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        service.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Simulación: reemplaza esto por tu lógica real de JWT
    private int extractUserIdFromJwt(String authHeader) {
        // Por ejemplo, si el token es "Bearer <id>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                return Integer.parseInt(authHeader.substring(7));
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    

}
