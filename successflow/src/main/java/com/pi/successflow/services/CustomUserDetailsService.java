package com.pi.successflow.services;

import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(input)
                .or(() -> usuarioRepository.findByUsername(input))
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + input)
                );

        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getContrasenia(),
                new ArrayList<>()
        );
    }
}