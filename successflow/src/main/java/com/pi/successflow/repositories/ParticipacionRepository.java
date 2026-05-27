package com.pi.successflow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pi.successflow.entity.Participacion;

@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, Integer> {

    @Query("SELECT p FROM Participacion p WHERE p.usuario.id_usuario = :usuarioId")
    List<Participacion> findByUsuario(@Param("usuarioId") int usuarioId);

    @Query("SELECT p FROM Participacion p WHERE p.proyecto.id = :proyectoId")
    List<Participacion> findByProyecto(@Param("proyectoId") int proyectoId);

    @Query("SELECT p FROM Participacion p WHERE p.usuario.id_usuario = :usuarioId AND p.proyecto.id = :proyectoId")
    Optional<Participacion> findByUsuarioAndProyecto(
            @Param("usuarioId") int usuarioId,
            @Param("proyectoId") int proyectoId
    );
}
