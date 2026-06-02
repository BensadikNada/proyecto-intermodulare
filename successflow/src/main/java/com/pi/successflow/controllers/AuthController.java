package com.pi.successflow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pi.successflow.entity.Usuario;
import com.pi.successflow.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final UsuarioService usuarioService;

    // ----Login Page----
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "expired", required = false) String expired,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }

        if (expired != null) {
            model.addAttribute("error", "Your session has expired. Please login again.");
        }

        return "auth/Login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/Register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute Usuario usuario, Model model) {

        if (usuarioService.crearUsuario(usuario) == null) {
            model.addAttribute("error", "Correo ya existe");
            return "auth/Register";
        }

        return "redirect:/auth/login?registrado=true";
    }
}
