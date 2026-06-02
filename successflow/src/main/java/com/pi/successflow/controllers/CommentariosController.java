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

import com.pi.successflow.entity.Ticket;
import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.UsuarioRepository;
import com.pi.successflow.services.TicketService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/comentario")
@RequiredArgsConstructor
public class CommentariosController {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/listar-comentarios-tarea/{id_tarea}")
    public String ListarComentariosTarea(@PathVariable int id_tarea, Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(email).orElseThrow();
        List<Ticket> tickets = ticketService.getTicketsByTarea(id_tarea);
        String nombre = usuario.getNombre();
        String[] firstLetter = nombre.split("");
        System.out.println("id tarea: " + id_tarea);
        model.addAttribute("tickets", tickets);
        model.addAttribute("firstLetter", firstLetter[0]);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tareaId", id_tarea);
        return "comentarios/ListarComentarios";
    }

    @GetMapping("/crear-comentario/{tareaId}")
    public String CrearComentarioPage(@PathVariable int tareaId, Model model){
        model.addAttribute("tareaId", tareaId);
        return "comentarios/CrearComentario";
    }

    @PostMapping("/crear-comentario/{tareaId}")
    public String CrearComentario(@ModelAttribute Ticket t, @PathVariable int tareaId, Authentication authentication, Model m){
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        Ticket ticketCreado = ticketService.crearTicket(t, tareaId, u.getId_usuario());
        if (ticketCreado != null) {
            return "redirect:/comentario/listar-comentarios-tarea/" + tareaId;
        } else {
            m.addAttribute("error", "No se pudo crear el comentario");
            return "comentarios/CrearComentario";
        }
    }

    @GetMapping("/editar-comentario/{id_ticket}/{tareaId}")
    public String EditarComentarioPage(@PathVariable int id_ticket, Model m, @PathVariable int tareaId){
        Ticket t = ticketService.getTicketById(id_ticket);
        m.addAttribute("ticket", t);
        m.addAttribute("tareaId", tareaId);
        return "comentarios/EditarComentario";
    }

    @PostMapping("/editar-comentario/{id_ticket}/{tareaId}")
    public String EditarComentario(@PathVariable int id_ticket, @ModelAttribute Ticket t, Model m, @PathVariable int tareaId){
        Ticket ticketEditado = ticketService.editarTicket(id_ticket, t);
        if (ticketEditado != null) {
            return "redirect:/comentario/listar-comentarios-tarea/" + tareaId;
        } else {
            m.addAttribute("error", "No se pudo editar el comentario");
            return "comentarios/EditarComentario";
        }
    }

    @PostMapping("/eliminar-comentario/{id_ticket}/{tareaId}")
    public String EliminarComentario(@PathVariable int id_ticket, Model m, @PathVariable int tareaId){
        boolean eliminado = ticketService.eliminarTicket(id_ticket);
        if (eliminado) {
            return "redirect:/comentario/listar-comentarios-tarea/" + tareaId;
        } else {
            m.addAttribute("error", "No se pudo eliminar el comentario");
            return "comentarios/ListarComentarios";
        }
    }
}
