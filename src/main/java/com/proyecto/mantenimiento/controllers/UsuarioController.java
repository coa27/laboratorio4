package com.proyecto.mantenimiento.controllers;

import com.proyecto.mantenimiento.payload.request.LoginReqRecord;
import com.proyecto.mantenimiento.payload.request.ValidarTokenReqRecord;
import com.proyecto.mantenimiento.payload.response.LoginResRecord;
import com.proyecto.mantenimiento.services.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UsuarioController {

    @Autowired
    private IUsuariosService service;

    @PostMapping("/acceder")
    public ResponseEntity<LoginResRecord> iniarSesion(@RequestBody @Validated LoginReqRecord loginReqRecord){
        LoginResRecord res = service.acceder(loginReqRecord);

        return new ResponseEntity<LoginResRecord>(res, HttpStatus.OK);
    }

    @PostMapping("/registrar")
    public ResponseEntity<LoginResRecord> registrarUsuario(@RequestBody @Validated LoginReqRecord registroReqRecord){
        return new ResponseEntity<LoginResRecord>(service.registro(registroReqRecord), HttpStatus.CREATED);
    }

    @PostMapping("/validar")
    public ResponseEntity<LoginResRecord> validarToken(@RequestBody @Validated ValidarTokenReqRecord token){
        LoginResRecord tokenNuevo = service.validarToken(token);

        return new ResponseEntity<LoginResRecord>(tokenNuevo, HttpStatus.OK);
    }

}
