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

    //-----crear ticket-----
    public Ticket crearTicket(Ticket t, Tarea tareaId, Usuario usuarioId) {
        Tarea tarea = tareaRepository.findById(tareaId.getId_tarea()).orElse(null);
        Usuario usuario = usuarioRepository.findById(usuarioId.getId_usuario()).orElse(null);
        t.setTarea(tarea);
        t.setUsuario(usuario);
        return ticketRepository.save(t);
    }

    //-----editar ticket-----
    public Ticket editarTicket(int id, Ticket t) {
        Ticket ticketExistente = ticketRepository.findById(id).orElse(null);

        if (ticketExistente == null) {
            return null;
        }

        if (t.getTitulo() != null) {
            ticketExistente.setTitulo(t.getTitulo());
        }
        if (t.getDescripcion() != null) {
            ticketExistente.setDescripcion(t.getDescripcion());
        }

        return ticketRepository.save(ticketExistente);
    }

    //-----eliminar ticket-----
    public boolean eliminarTicket(int id) {
        if (!ticketRepository.existsById(id)) {
            return false;
        }
        ticketRepository.deleteById(id);
        return true;
    }

    //-----buscar tickets por tarea-----
    public List<Ticket> getTicketsByTarea(int tareaId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getTarea() != null && ticket.getTarea().getId_tarea() == tareaId)
                .collect(Collectors.toList());
    }

    //-----buscar tickets por usuario-----
    public List<Ticket> getTicketsByUsuario(int usuarioId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getUsuario() != null && ticket.getUsuario().getId_usuario() == usuarioId)
                .collect(Collectors.toList());
    }
}
