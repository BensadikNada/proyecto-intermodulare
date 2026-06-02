package com.pi.successflow.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // -----crear usuario-----
    public Usuario crearUsuario(Usuario u) {
        if (usuarioRepository.findByCorreo(u.getCorreo()).isPresent()) {
            return null;
        }
        u.setContrasenia(passwordEncoder.encode(u.getContrasenia()));
        return usuarioRepository.save(u);
    }

    // -----buscar usuario por su id-----
    public Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // -----editar usuario-----
    public Usuario editarUsuario(Usuario u) {
        Usuario usuarioExistente = usuarioRepository.findById(u.getId_usuario())
                .orElse(null);

        if (usuarioExistente == null) {
            return null;
        }

        if (u.getNombre() != null) {
            usuarioExistente.setNombre(u.getNombre());
        }
        if (u.getApellidos() != null) {
            usuarioExistente.setApellidos(u.getApellidos());
        }
        if (u.getCorreo() != null) {
            usuarioExistente.setCorreo(u.getCorreo());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    // -----eliminar usuario-----
    public boolean eliminarUsuario(int id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    // -----cambiar contrasenia-----
    public boolean cambiarContrasenia(int id, String nuevaContrasenia) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElse(null);
        if (usuarioExistente == null) {
            return false;
        }
        usuarioExistente.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        usuarioRepository.save(usuarioExistente);
        return true;
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElse(null);
    }

    public boolean verificarContrasena(Usuario u, String rawPassword) {
        return passwordEncoder.matches(rawPassword, u.getContrasenia());
    }
}
