package com.pi.successflow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pi.successflow.entity.Usuario;
import com.pi.successflow.repositories.UsuarioRepository;
import com.pi.successflow.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String perfil(Authentication authentication, Model m) {
        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();
        String nombre = u.getNombre();
        String[] firstLetter = nombre.split("");
        m.addAttribute("usuario", u);
        m.addAttribute("firstLetter", firstLetter[0]);
        return "Perfil";
    }

    @GetMapping("/cambiar-contrasenia")
    public String cambiarContrasena() {
        return "CambiarContrasena";
    }

    @PostMapping("/cambiar-contrasenia")
    public String cambiarContrasena(
            Authentication authentication,
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model m) {

        String email = authentication.getName();
        Usuario u = usuarioRepository.findByCorreo(email).orElseThrow();

        if (!usuarioService.verificarContrasena(u, contrasenaActual)) {
            m.addAttribute("error", "La contraseña actual es incorrecta.");
            return "CambiarContrasena";
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            m.addAttribute("error", "Las nuevas contraseñas no coinciden.");
            return "CambiarContrasena";
        }

        usuarioService.cambiarContrasenia(u.getId_usuario(), nuevaContrasena);

        m.addAttribute("success", "Contraseña cambiada exitosamente.");
        return "CambiarContrasena";
    }
}