package com.pi.successflow.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pi.successflow.entity.Proyecto;
import com.pi.successflow.entity.Tarea;
import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.TareaRepository;
import com.pi.successflow.repositories.UsuarioRepository;
import com.pi.successflow.services.TareaService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/tarea")
@RequiredArgsConstructor
public class TareaController {
    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @GetMapping("/listar-todo")
    public String listarTodo(Model m, Authentication authentication) {
        // ---listar usuario----
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        List<Tarea> tareas = tareaService.listarPorUsuario(u.getId_usuario());
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        m.addAttribute("tareas", tareas);
        m.addAttribute("firstLetter", firstLetter[0]);
        m.addAttribute("usuario", u);
        return "tarea/ListarTodo";
    }

    @GetMapping("/crear")
    public String crearPage(Model m) {
        m.addAttribute("tarea", new Tarea());
        return "tarea/CrearTarea";
    }

    @PostMapping("/crear")
    public String crearTarea(@ModelAttribute Tarea t, Model m, Authentication authentication) {
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();

        if (tareaService.crearTarea(t, null, u.getId_usuario()) == null) {
            m.addAttribute("error", "Fallo al agregar de la tarea");
            return "CrearTarea";
        }

        return "redirect:/tarea/listar-todo";
    }

    @GetMapping("/editar/{id_tarea}")
    public String editarPage(Model m, @PathVariable int id_tarea) {
        Tarea t = tareaService.buscarPorId(id_tarea);
        if (t == null) {
            m.addAttribute("error", "No ha encontrado la tarea con el id" + id_tarea);
            return "ListarTodo";
        }
        m.addAttribute("t", t);
        return "tarea/EditarTarea";
    }

    @PostMapping("/editar/{id_tarea}")
    public String editarTarea(@ModelAttribute Tarea t, @PathVariable int id_tarea, Model m) {
        if (tareaService.editarTarea(id_tarea, t, null) == null) {
            m.addAttribute("error", "Fallo al editar de la tarea");
            return "EditarTarea";
        }
        return "redirect:/tarea/listar-todo";
    }

    @PostMapping("/eliminar/{id_tarea}")
    public String eliminarTarea(@PathVariable int id_tarea) {
        tareaService.eliminarTarea(id_tarea);
        return "redirect:/tarea/listar-todo";
    }

    @PostMapping("/editar-estado/{id_tarea}")
    public String editarEstado(@PathVariable int id_tarea){
        Tarea t = tareaService.buscarPorId(id_tarea);
        t.setEstado("hecha");
        tareaRepository.save(t);
        return "redirect:/tarea/listar-todo";
    }
}
