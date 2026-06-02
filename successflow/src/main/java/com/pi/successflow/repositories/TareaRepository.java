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

        @Query("""
                        SELECT t
                        FROM Tarea t
                        JOIN t.usuarios u
                        WHERE u.id_usuario = :usuarioId
                        AND t.estado = :estado
                        """)
        List<Tarea> findByUsuarioIdAndEstado(
                        @Param("usuarioId") int usuarioId,
                        @Param("estado") String estado);

        // @Query("SELECT t FROM Tarea t JOIN t.usuarios u WHERE u.id_usuario =
        // :usuarioId AND t.proyecto_id = :proyectoId")
        // List<Tarea> findByUsuarioIdYProyectoId(@Param("usuarioId") int usuarioId,
        // @Param("proyectoId") int proyectoId);

        @Query("SELECT t FROM Tarea t WHERE t.estado = :estado AND t.proyecto.id = :proyectoId")
        List<Tarea> findByEstadoYProyecto(
                        @Param("estado") String estado,
                        @Param("proyectoId") int proyectoId);
}