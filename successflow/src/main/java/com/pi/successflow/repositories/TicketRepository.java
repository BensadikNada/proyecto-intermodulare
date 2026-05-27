package com.pi.successflow.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pi.successflow.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("""
        SELECT t
        FROM Ticket t
        WHERE t.usuario.id_usuario = :usuarioId
    """)
    List<Ticket> findByUsuario(@Param("usuarioId") int usuarioId);

    @Query("""
        SELECT t
        FROM Ticket t
        WHERE t.tarea.id = :tareaId
    """)
    List<Ticket> findByTarea(@Param("tareaId") int tareaId);

    @Query("""
        SELECT t
        FROM Ticket t
        WHERE t.tarea.id = :tareaId
        AND t.usuario.id_usuario = :usuarioId
    """)
    List<Ticket> findByTareaAndUsuario(
            @Param("tareaId") int tareaId,
            @Param("usuarioId") int usuarioId
    );
}