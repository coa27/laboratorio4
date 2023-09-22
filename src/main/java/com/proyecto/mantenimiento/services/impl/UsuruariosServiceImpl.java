package com.proyecto.mantenimiento.services.impl;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.payload.request.LoginReqRecord;
import com.proyecto.mantenimiento.payload.request.ValidarTokenReqRecord;
import com.proyecto.mantenimiento.payload.response.LoginResRecord;
import com.proyecto.mantenimiento.repos.IUsuariosRepo;
import com.proyecto.mantenimiento.security.jwt.TokenService;
import com.proyecto.mantenimiento.security.manager.ManagerDeAutenticacion;
import com.proyecto.mantenimiento.services.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UsuruariosServiceImpl implements IUsuariosService {


    @Autowired
    private ManagerDeAutenticacion managerDeAutenticacion;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUsuariosRepo repo;


    @Override
    public LoginResRecord acceder(LoginReqRecord loginReqRecord) {

        var authentication =  managerDeAutenticacion.authenticate(
                new UsernamePasswordAuthenticationToken(loginReqRecord.email(), loginReqRecord.password())
        );

        String token = tokenService.generarToken(loginReqRecord.email());

        return new LoginResRecord(loginReqRecord.email(), token);

    }

    @Override
    public LoginResRecord registro(LoginReqRecord registroReqRecord) {
        if(repo.findByEmail(registroReqRecord.email()).isPresent()){
            throw new RuntimeException("El email ya esta en uso");
        }

        Usuario usuario = new Usuario(registroReqRecord.email(), passwordEncoder.encode(registroReqRecord.password()));
        repo.save(usuario);
        repo.guardarRol(usuario.getIdUsuario());

        String token = tokenService.generarToken(usuario.getEmail());

        return new LoginResRecord(registroReqRecord.email(), token);

    }

    @Override
    public LoginResRecord validarToken(ValidarTokenReqRecord token) {

        DecodedJWT decodedJWT = tokenService.validarJwt(token.token());
        String tokenNuevo;

        //Si al token le faltan 30 minutos para expirar, se crea un nuevo token
        if ((Instant.now().until(decodedJWT.getExpiresAtAsInstant(), ChronoUnit.MINUTES)) < 30){
            String email =  decodedJWT.getClaim("idUsuario").toString();
            tokenNuevo = tokenService.generarToken(email);
            return new LoginResRecord(null, tokenNuevo);
        }

        //si no,se devuelve el mismo token validado
        tokenNuevo = token.token();
        return new LoginResRecord(null, tokenNuevo);

    }
}
