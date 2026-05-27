package com.pi.successflow.entity;

import java.time.LocalDate;
import java.util.List;

import com.pi.successflow.enumeration.TipoTarea;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tareas")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_tarea;

    private String titulo;
    private String descripcion;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private String estado;
    @Enumerated(EnumType.STRING)
    private TipoTarea tipo;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    @ManyToMany(mappedBy = "tareas")
    @ToString.Exclude
    private List<Usuario> usuarios;
}
