package com.pi.successflow.entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_usuario;

    @Column(unique = true)
    private String username;
    private String nombre;
    private String apellidos;
    @Column(unique = true)
    private String correo;
    private String contrasenia;

    @OneToMany(mappedBy = "usuario")
    private List<Participacion> participaciones;

    @ManyToMany
    @JoinTable(name = "usuario_tarea", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "tarea_id"))
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Tarea> tareas;
}
