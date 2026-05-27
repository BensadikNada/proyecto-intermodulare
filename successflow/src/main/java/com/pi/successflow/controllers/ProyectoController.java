package com.pi.successflow.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pi.successflow.entity.Proyecto;
import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.UsuarioRepository;
import com.pi.successflow.services.ProyectoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/proyecto")
@RequiredArgsConstructor
public class ProyectoController {
    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/listar-proyectos")
    public String listarProyectos(Model m, Authentication authentication){
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        List<Proyecto> proyectos =  proyectoService.listarPorUsuario(u.getId_usuario());
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        int countMiembros = 0;
        for (Proyecto proyecto : proyectos) {
            countMiembros = proyectoService.countMiembrosProyecto(proyecto.getId());
        }
        m.addAttribute("firstLetter", firstLetter[0]);
        m.addAttribute("proyectos", proyectos);
        m.addAttribute("usuario", u);
        m.addAttribute("countMiembros", countMiembros);
        return "proyecto/ListarProyectos";
    }

    @GetMapping("/crear")
    public String crearPage(Model m){
        m.addAttribute("proyecto", new Proyecto());
        return "proyecto/CrearProyecto";
    }

    @PostMapping("/crear")
    public String crearProyecto(@ModelAttribute Proyecto p, Model m, Authentication authentication){
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();

        if (proyectoService.crearProyecto(p, u.getId_usuario()) == null) {
            m.addAttribute("error", "Fallo al agregar de el proyecto");
            return "proyecto/CrearProyecto";
        }

        return "redirect:/proyecto/listar-proyectos";
    }

    @GetMapping("/editar/{id_proyecto}")
    public String editarPage(Model m, @PathVariable int id_proyecto){
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        m.addAttribute("p", p);
        return "proyecto/EditarProyecto";
    }

    @PostMapping("/editar/{id_proyecto}")
    public String editarProyecto(@ModelAttribute Proyecto p, @PathVariable int id_proyecto, Model m){
        if (proyectoService.editarProyecto(id_proyecto, p) == null) {
            m.addAttribute("error", "Fallo al editar del proyecto");
            return "proyecto/EditarProyecto";
        }
        return "redirect:/proyecto/listar-proyectos";
    }

    @PostMapping("/eliminar/{id_proyecto}")
    public String eliminarProyecto(@PathVariable int id_proyecto){
        proyectoService.eliminarProyecto(id_proyecto);
        return "redirect:/proyecto/listar-proyectos";
    }

    @GetMapping("/proyectoConId/{id_proyecto}")
    public String listarProyectoConId(@PathVariable int id_proyecto, Model m, Authentication authentication){
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        m.addAttribute("p", p);
        m.addAttribute("firstLetter", firstLetter[0]);
        m.addAttribute("usuario", u);
        return "proyecto/FetchProyectoConId";
    }
}
