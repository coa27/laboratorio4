package com.proyecto.mantenimiento.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hola")
public class PruebaController {

    @GetMapping
    public String hola() {
        return "hola: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
