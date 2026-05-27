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
    public String loginPage() {
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
            model.addAttribute("error", "Email already registered");
            return "auth/Register";
        }

        return "redirect:/auth/login?registrado=true";
    }
}
