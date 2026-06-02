package com.pi.successflow.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pi.successflow.entity.*;
import com.pi.successflow.enumeration.TipoTarea;
import com.pi.successflow.repositories.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    // private functiion to apply project rules when creating/editing a task
    // (depending on type)
    private void applyProyectoIfNeeded(Tarea tarea, TipoTarea tipo, Integer proyectoId) {

        // if task is compartida → MUST belong to a project
        if (tipo == TipoTarea.compartida) {

            if (proyectoId == null) {
                throw new RuntimeException("Tarea compartida necesita un proyecto");
            }

            Proyecto proyecto = proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

            tarea.setProyecto(proyecto);

        } else {
            // INDIVIDUAL task → no project
            tarea.setProyecto(null);
        }
    }

    // create task
    public Tarea crearTarea(Tarea t, Integer proyectoId, int usuarioId) {

        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow();

        applyProyectoIfNeeded(t, t.getTipo(), proyectoId);

        Tarea tareaGuardada = tareaRepository.save(t);

        if (u.getTareas() == null) {
            u.setTareas(new ArrayList<>());
        }
        u.getTareas().add(tareaGuardada);

        usuarioRepository.save(u);

        return tareaGuardada;
    }

    // crear tarea in proyecto
    public Tarea crearTareaEnProyecto(
            Tarea tarea,
            int proyectoId,
            List<Integer> usuariosIds) {

        Proyecto proyecto = proyectoRepository
                .findById(proyectoId)
                .orElseThrow();

        tarea.setProyecto(proyecto);

        List<Usuario> usuarios = usuarioRepository.findAllById(usuariosIds);

        tarea.setUsuarios(usuarios);

        return tareaRepository.save(tarea);
    }

    // edit task
    public Tarea editarTarea(int id, Tarea t, Integer proyectoId) {

        Tarea tareaExistente = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Update only non-null fields (PATCH behavior)
        if (t.getTitulo() != null)
            tareaExistente.setTitulo(t.getTitulo());

        if (t.getDescripcion() != null)
            tareaExistente.setDescripcion(t.getDescripcion());

        if (t.getEstado() != null)
            tareaExistente.setEstado(t.getEstado());

        if (t.getFecha_inicio() != null)
            tareaExistente.setFecha_inicio(t.getFecha_inicio());

        if (t.getFecha_fin() != null)
            tareaExistente.setFecha_fin(t.getFecha_fin());

        if (t.getTipo() != null)
            tareaExistente.setTipo(t.getTipo());

        // Re-apply project rules in case type or projectId changed
        applyProyectoIfNeeded(tareaExistente, tareaExistente.getTipo(), proyectoId);

        return tareaRepository.save(tareaExistente);
    }

    // editar tarea en proyecto
    public Tarea editarTareaProyecto(int id, Tarea t, Integer proyectoId, List<Integer> usuariosIds) {

        Tarea tareaExistente = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (t.getTitulo() != null)
            tareaExistente.setTitulo(t.getTitulo());

        if (t.getDescripcion() != null)
            tareaExistente.setDescripcion(t.getDescripcion());

        if (t.getEstado() != null)
            tareaExistente.setEstado(t.getEstado());

        if (t.getFecha_inicio() != null)
            tareaExistente.setFecha_inicio(t.getFecha_inicio());

        if (t.getFecha_fin() != null)
            tareaExistente.setFecha_fin(t.getFecha_fin());

        if (t.getTipo() != null)
            tareaExistente.setTipo(t.getTipo());

        if (proyectoId != null) {
            Proyecto proyecto = proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            tareaExistente.setProyecto(proyecto);
        }

        if (usuariosIds != null) {
            List<Usuario> usuarios = usuarioRepository.findAllById(usuariosIds);

            tareaExistente.setUsuarios(usuarios);

            // AUTO TYPE RULE
            tareaExistente.setTipo(
                    usuarios.size() > 1
                            ? TipoTarea.compartida
                            : TipoTarea.individual);
        }

        return tareaRepository.save(tareaExistente);
    }

    // delete tarea
    @Transactional
    public void eliminarTarea(int id) {

        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // remove the realcion befor deleting
        tarea.getUsuarios().clear();
        tareaRepository.delete(tarea);
    }

    // list all tareas
    public List<Tarea> listarTodas() {
        return tareaRepository.findAll();
    }

    // find tarea by id
    public Tarea buscarPorId(int id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    // list individual task
    public List<Tarea> listarIndividuales() {
        return tareaRepository.findByTipo(TipoTarea.individual);
    }

    // list compartida task
    public List<Tarea> listarCompartidas() {
        return tareaRepository.findByTipo(TipoTarea.compartida);
    }

    // list task by id project
    public List<Tarea> listarPorProyecto(int proyectoId) {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        return tareaRepository.findAll()
                .stream()
                .filter(t -> t.getProyecto() != null
                        && t.getProyecto().getId() == proyecto.getId())
                .toList();
    }

    // list task by id user
    public List<Tarea> listarPorUsuario(int usuarioId) {
        return tareaRepository.findByUsuarioId(usuarioId);
    }

    public int countTareasUdsuario(int usuario_id) {
        return tareaRepository.findByUsuarioId(usuario_id).size();
    }

    public int countTareasConEstado(int usuarioId, String estado) {
        return tareaRepository
                .findByUsuarioIdAndEstado(usuarioId, estado)
                .size();
    }

    public List<Tarea> findByEstadoYProyecto(int proyecto_id, String estado) {
        return tareaRepository.findByEstadoYProyecto(estado, proyecto_id);
    }

    public void actualizarEstado(int idTarea, String estado) {
        Tarea t = tareaRepository.findById(idTarea)
                .orElseThrow();

        t.setEstado(estado);
        tareaRepository.save(t);
    }
}