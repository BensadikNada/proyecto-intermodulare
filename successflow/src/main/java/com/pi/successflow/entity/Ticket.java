package com.pi.successflow.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_ticket;

    private String titulo;
    private String descripcion;
    private LocalDate fecha_creacion;

    @ManyToOne
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
