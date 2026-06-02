package com.pi.successflow.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pi.successflow.entity.*;
import com.pi.successflow.repositories.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;

    // -----crear ticket-----
    public Ticket crearTicket(Ticket t, int tareaId, int usuarioId) {
        Tarea tarea = tareaRepository.findById(tareaId).orElse(null);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        t.setTarea(tarea);
        t.setUsuario(usuario);
        return ticketRepository.save(t);
    }

    public Ticket getTicketById(int id_ticket) {
        return ticketRepository.findById(id_ticket).orElse(null);
    }

    // -----editar ticket-----
    public Ticket editarTicket(int id_ticket, Ticket t) {
        Ticket ticketExistente = ticketRepository.findById(id_ticket).orElse(null);

        if (ticketExistente == null) {
            return null;
        }

        if (t.getTitulo() != null) {
            ticketExistente.setTitulo(t.getTitulo());
        }
        if (t.getDescripcion() != null) {
            ticketExistente.setDescripcion(t.getDescripcion());
        }

        if (t.getTarea() != null) {
            ticketExistente.setTarea(t.getTarea());
        }

        if (t.getUsuario() != null) {
            ticketExistente.setUsuario(t.getUsuario());
        }

        if (t.getFecha_creacion() != null) {
            ticketExistente.setFecha_creacion(t.getFecha_creacion());
        }

        return ticketRepository.save(ticketExistente);
    }

    // -----eliminar ticket-----
    public boolean eliminarTicket(int id) {
        if (!ticketRepository.existsById(id)) {
            return false;
        }
        ticketRepository.deleteById(id);
        return true;
    }

    // -----buscar tickets por tarea-----
    public List<Ticket> getTicketsByTarea(int tareaId) {
        return ticketRepository.findByTarea(tareaId);
    }

    // -----buscar tickets por usuario-----
    public List<Ticket> getTicketsByUsuario(int usuarioId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getUsuario() != null && ticket.getUsuario().getId_usuario() == usuarioId)
                .collect(Collectors.toList());
    }
}
