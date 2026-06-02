package com.pi.successflow.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pi.successflow.entity.Participacion;
import com.pi.successflow.entity.Proyecto;
import com.pi.successflow.entity.Usuario;
import com.pi.successflow.enumeration.Rol;
import com.pi.successflow.repositories.ParticipacionRepository;
import com.pi.successflow.repositories.ProyectoRepository;
import com.pi.successflow.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProyectoService {
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipacionRepository participacionRepository;

    // ----crear proyecto y asignarlo el owner----
    public Proyecto crearProyecto(Proyecto p, int admin_id) {

        // find the user if exists
        Usuario admin = usuarioRepository.findById(admin_id)
                .orElseThrow(() -> new RuntimeException("Usuario admin no encontrado"));

        Proyecto proyectoGuardado = proyectoRepository.save(p);

        // asigne the admin of the project created
        Participacion adminPart = new Participacion();
        adminPart.setUsuario(admin);
        adminPart.setProyecto(proyectoGuardado);
        adminPart.setRol(Rol.admin);
        participacionRepository.save(adminPart);

        // looping through the list of ids of users to add them as members of the
        // project created
        // for (Integer idu : ids_usuarios) {

        // // avoid adding owner again
        // if (idu == admin_id)
        // continue;

        // Usuario miembro = usuarioRepository.findById(idu)
        // .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idu));

        // Participacion participacion = new Participacion();
        // participacion.setUsuario(miembro);
        // participacion.setProyecto(proyectoGuardado);
        // participacion.setRol(Rol.miembro);

        // participacionRepository.save(participacion);
        // }

        return proyectoGuardado;
    }

    public void anadirUsuarioConCorreo(String email, int idProyecto) {
        Usuario miembro = usuarioRepository.findByCorreo(email).orElseThrow();
        Proyecto proyecto = proyectoRepository.findById(idProyecto).orElseThrow();
        Participacion participacion = new Participacion();
        participacion.setUsuario(miembro);
        participacion.setProyecto(proyecto);
        participacion.setRol(Rol.miembro);
        participacionRepository.save(participacion);
    }

    // -----get all the projects-----
    public List<Proyecto> listarTodosProyectos() {
        return proyectoRepository.findAll();
    }

    // -----get projects by the user id-----
    public List<Proyecto> listarPorUsuario(int usuarioId) {

        List<Participacion> participaciones = participacionRepository.findByUsuario(usuarioId);

        return participaciones.stream()
                .map(Participacion::getProyecto)
                .toList();
    }

    // -----editar proyecto-----
    public Proyecto editarProyecto(int id, Proyecto p) {

        Proyecto proyectoExistente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (p.getTitulo() != null) {
            proyectoExistente.setTitulo(p.getTitulo());
        }

        if (p.getDescripcion() != null) {
            proyectoExistente.setDescripcion(p.getDescripcion());
        }

        if (p.getEstado() != null) {
            proyectoExistente.setEstado(p.getEstado());
        }

        if (p.getFecha_creation() != null) {
            proyectoExistente.setFecha_creation(p.getFecha_creation());
        }

        return proyectoRepository.save(proyectoExistente);
    }

    // ----eliminar proyecto----
    public boolean eliminarProyecto(int id) {
        if (!proyectoRepository.existsById(id)) {
            return false;
        }
        proyectoRepository.deleteById(id);
        return true;
    }

    // ----contar proyectos de usuario----
    public int countPtoyectoUsuario(int usuario_id) {
        return listarPorUsuario(usuario_id).size();
    }

    public Proyecto buscarPorId(int id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    public int countMiembrosProyecto(int id_proyecto) {
        return participacionRepository.findByProyecto(id_proyecto).size();
    }

    public List<Usuario> getMiembrosProyecto(int id_proyecto) {
        return participacionRepository.findUsuariosByProyecto(id_proyecto);
    }
}
