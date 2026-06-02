package com.pi.successflow.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pi.successflow.entity.Proyecto;
import com.pi.successflow.entity.Tarea;
import com.pi.successflow.entity.Usuario;
import com.pi.successflow.enumeration.TipoTarea;
import com.pi.successflow.repositories.UsuarioRepository;
import com.pi.successflow.services.ProyectoService;
import com.pi.successflow.services.TareaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/proyecto")
@RequiredArgsConstructor
public class ProyectoController {
    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareaService tareaService;

    @GetMapping("/listar-proyectos")
    public String listarProyectos(Model m, Authentication authentication) {
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        List<Proyecto> proyectos = proyectoService.listarPorUsuario(u.getId_usuario());
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        Map<Integer, Integer> countMiembros = new HashMap<>();
        for (Proyecto proyecto : proyectos) {
            countMiembros.put(
                    proyecto.getId(),
                    proyectoService.countMiembrosProyecto(proyecto.getId()));
        }
        m.addAttribute("firstLetter", firstLetter[0]);
        m.addAttribute("proyectos", proyectos);
        m.addAttribute("usuario", u);
        m.addAttribute("countMiembros", countMiembros);
        return "proyecto/ListarProyectos";
    }

    @GetMapping("/crear")
    public String crearPage(Model m) {
        m.addAttribute("proyecto", new Proyecto());
        return "proyecto/CrearProyecto";
    }

    @PostMapping("/crear")
    public String crearProyecto(@ModelAttribute Proyecto p, Model m, Authentication authentication) {
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();

        if (proyectoService.crearProyecto(p, u.getId_usuario()) == null) {
            m.addAttribute("error", "Fallo al agregar de el proyecto");
            return "proyecto/CrearProyecto";
        }

        return "redirect:/proyecto/listar-proyectos";
    }

    @GetMapping("/editar/{id_proyecto}")
    public String editarPage(Model m, @PathVariable int id_proyecto) {
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        m.addAttribute("p", p);
        return "proyecto/EditarProyecto";
    }

    @PostMapping("/editar/{id_proyecto}")
    public String editarProyecto(@ModelAttribute Proyecto p, @PathVariable int id_proyecto, Model m) {
        if (proyectoService.editarProyecto(id_proyecto, p) == null) {
            m.addAttribute("error", "Fallo al editar del proyecto");
            return "proyecto/EditarProyecto";
        }
        return "redirect:/proyecto/listar-proyectos";
    }

    @PostMapping("/eliminar/{id_proyecto}")
    public String eliminarProyecto(@PathVariable int id_proyecto) {
        proyectoService.eliminarProyecto(id_proyecto);
        return "redirect:/proyecto/listar-proyectos";
    }

    @GetMapping("/proyectoConId/{id_proyecto}")
    public String listarProyectoConId(@PathVariable int id_proyecto, Model m, Authentication authentication) {
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        int numMiembros = proyectoService.countMiembrosProyecto(id_proyecto);
        List<Usuario> listaUsuario = proyectoService.getMiembrosProyecto(id_proyecto);
        ArrayList<String> lettersUsuairos = new ArrayList<>();
        for (Usuario usuario : listaUsuario) {
            String nombre = usuario.getNombre();
            String[] separar = nombre.split("");
            lettersUsuairos.add(separar[0]);
        }
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        List<Tarea> tareasPendientes = tareaService.findByEstadoYProyecto(id_proyecto, "pendiente");
        List<Tarea> tareasEnProseso = tareaService.findByEstadoYProyecto(id_proyecto, "en_progreso");
        List<Tarea> tareasHecha = tareaService.findByEstadoYProyecto(id_proyecto, "hecha");
        m.addAttribute("p", p);
        m.addAttribute("numMiembros", numMiembros);
        m.addAttribute("lettersUsuairos", lettersUsuairos);
        m.addAttribute("firstLetter", firstLetter[0]);
        m.addAttribute("usuario", u);
        m.addAttribute("tareasPendientes", tareasPendientes.size());
        m.addAttribute("tareasEnProseso", tareasEnProseso.size());
        m.addAttribute("tareasHecha", tareasHecha.size());
        m.addAttribute("pendiente", tareasPendientes);
        m.addAttribute("enProceso", tareasEnProseso);
        m.addAttribute("hecha", tareasHecha);
        return "proyecto/FetchProyectoConId";
    }

    @GetMapping("/anadir-miembro/{id_proyecto}")
    public String anadirMiembroPage(@PathVariable int id_proyecto, Model m) {
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        if (p == null) {
            return "redirect:/proyecto/listar-proyectos";
        }
        m.addAttribute("p", p);
        return "proyecto/AnadirMiembro";
    }

    @PostMapping("/anadir-miembro/{id_proyecto}")
    public String anadirMiembro(@PathVariable int id_proyecto, @RequestParam("correo") String correo) {
        proyectoService.anadirUsuarioConCorreo(correo, id_proyecto);
        return "redirect:/proyecto/proyectoConId/" + id_proyecto;
    }

    @GetMapping("/crear-tarea/{id_proyecto}")
    public String crearTarea(Model m, @PathVariable int id_proyecto) {
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        List<Usuario> usuariosProyecto = proyectoService.getMiembrosProyecto(id_proyecto);
        m.addAttribute("tarea", new Tarea());
        m.addAttribute("p", p);
        m.addAttribute("usuariosProyecto", usuariosProyecto);
        return "proyecto/crearTarea";
    }

    @PostMapping("/crear-tarea/{id_proyecto}")
    public String crearTarea(
            @ModelAttribute Tarea t,
            @RequestParam("usuariosIds") List<Integer> usuariosIds,
            @PathVariable int id_proyecto,
            Authentication authentication,
            Model m) {

        if (usuariosIds.size() > 1) {
            t.setTipo(TipoTarea.compartida);
        } else {
            t.setTipo(TipoTarea.individual);
        }

        tareaService.crearTareaEnProyecto(
                t,
                id_proyecto,
                usuariosIds);

        return "redirect:/proyecto/proyectoConId/" + id_proyecto;
    }

    @GetMapping("/editar-tarea/{id_proyecto}/{id_tarea}")
    public String editarTarea(@PathVariable int id_tarea, Model m, @PathVariable int id_proyecto) {
        Proyecto p = proyectoService.buscarPorId(id_proyecto);
        Tarea tarea = tareaService.buscarPorId(id_tarea);

        if (tarea == null && p == null) {
            return "redirect:/proyecto/proyectoConId/" + id_proyecto;
        }

        List<Usuario> usuariosProyecto = proyectoService.getMiembrosProyecto(tarea.getProyecto().getId());

        m.addAttribute("t", tarea);
        m.addAttribute("p", p);
        m.addAttribute("usuariosProyecto", usuariosProyecto);

        return "proyecto/editarTarea";
    }

    @PostMapping("/editar-tarea/{id_proyecto}/{id_tarea}")
    public String actualizarTarea(
            @PathVariable int id_tarea,
            @PathVariable int id_proyecto,
            @ModelAttribute Tarea tareaActualizada,
            @RequestParam(value = "usuariosIds", required = false) List<Integer> usuariosIds) {

        tareaService.editarTareaProyecto(
                id_tarea,
                tareaActualizada,
                id_proyecto,
                usuariosIds);

        return "redirect:/proyecto/proyectoConId/" + id_proyecto;
    }

    @PostMapping("/cambiar-estado/{id_tarea}")
    public String cambiarEstado(
            @PathVariable int id_tarea,
            @RequestParam String estado,
            @RequestParam int id_proyecto) {

        tareaService.actualizarEstado(id_tarea, estado);

        return "redirect:/proyecto/proyectoConId/" + id_proyecto;
    }

    @PostMapping("/eliminar/{id_tarea}/{id_proyecto}")
    public String eliminarTarea(@PathVariable int id_tarea, @PathVariable int id_proyecto) {
        tareaService.eliminarTarea(id_tarea);
        return "redirect:/proyecto/proyectoConId/" + id_proyecto;
    }
}
