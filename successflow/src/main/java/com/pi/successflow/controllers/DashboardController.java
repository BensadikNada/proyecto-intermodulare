package com.pi.successflow.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pi.successflow.entity.Tarea;
import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.UsuarioRepository;
import com.pi.successflow.services.ProyectoService;
import com.pi.successflow.services.TareaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/dashboard")
    public String dashboard(Model m, Authentication authentication) {
        // ---listar usuario----
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        List<Tarea> tareas = tareaService.listarPorUsuario(u.getId_usuario());
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        // -----counts-----
        int countTareas = tareaService.countTareasUdsuario(u.getId_usuario());
        int countTareasCompletas = tareaService.countTareasConEstado(u.getId_usuario(), "hecha");
        int countTareasPendiente = tareaService.countTareasConEstado(u.getId_usuario(), "pendiente");
        int countProyectos = proyectoService.countPtoyectoUsuario(u.getId_usuario());

        m.addAttribute("firstLetter", firstLetter[0]);
        m.addAttribute("usuario", u);
        m.addAttribute("countTarea", countTareas);
        m.addAttribute("countTareaCompletas", countTareasCompletas);
        m.addAttribute("countTareaPendiente", countTareasPendiente);
        m.addAttribute("tareas", tareas);
        m.addAttribute("countProyectos", countProyectos);
        return "Dashboard";
    }
}
