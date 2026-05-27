package com.pi.successflow.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pi.successflow.entity.Tarea;
import com.pi.successflow.enumeration.TipoTarea;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByTipo(TipoTarea tipo);

    List<Tarea> findByEstado(String estado);

    @Query("SELECT t FROM Tarea t JOIN t.usuarios u WHERE u.id_usuario = :usuarioId")
    List<Tarea> findByUsuarioId(@Param("usuarioId") int usuarioId);

    // @Query("SELECT t FROM Tarea t JOIN t.usuarios u WHERE u.id_usuario = :usuarioId AND t.id_proyecto = :proyectoId")
    // List<Tarea> findByUsuarioIdYProyectoId(@Param("usuarioId") int usuarioId, @Param("proyectoId") int proyectoId);
}