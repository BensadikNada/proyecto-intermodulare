package com.pi.successflow.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.successflow.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByUsername(String username);

    boolean existsByCorreo(String correo);
    boolean existsByUsername(String username);
}
