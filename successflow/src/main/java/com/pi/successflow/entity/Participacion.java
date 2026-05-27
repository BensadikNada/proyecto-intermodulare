package com.pi.successflow.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.pi.successflow.enumeration.Rol;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participaciones")
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Proyecto proyecto;

    @Enumerated(EnumType.STRING)
    private Rol rol;
}
