package com.uniquindio.triage_academy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SolicitudController {

    @GetMapping("/solicitudes")
    @ResponseBody
    public String obtenerSolicitudes(){
        return "todas las solicitudes";
    }

}
