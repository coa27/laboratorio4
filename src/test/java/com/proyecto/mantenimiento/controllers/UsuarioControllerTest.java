package com.proyecto.mantenimiento.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.mantenimiento.payload.request.LoginReqRecord;
import com.proyecto.mantenimiento.payload.request.ValidarTokenReqRecord;
import com.proyecto.mantenimiento.payload.response.LoginResRecord;
import com.proyecto.mantenimiento.repos.IUsuariosRepo;
import com.proyecto.mantenimiento.security.filter.CustomAuthenticationFilter;
import com.proyecto.mantenimiento.security.jwt.TokenService;
import com.proyecto.mantenimiento.services.IUsuariosService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUsuariosService service;

    @MockBean
    private IUsuariosRepo repo;

    @MockBean
    private CustomAuthenticationFilter customAuthenticationFilter;

    @MockBean
    private TokenService tokenService;

    private static LoginResRecord loginResRecord;
    @BeforeAll
    public static void setUp(){
        loginResRecord = new LoginResRecord("hola@hotmail.com", "token");
    }

    @Test
    public void dadoUsuarioValidoEnRegistro_returnExitoso() throws Exception {

        LoginReqRecord loginReqRecord = new LoginReqRecord("hola@hotmail.com", "12345");
        when(service.registro(any()))
                .thenReturn(loginResRecord);

        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(loginReqRecord))
                ).andExpect(status().is(201));
    }

    @Test
    public void dadoUsuarioNoValidoEnRegistro_returnExitoso() throws Exception {
        LoginReqRecord loginReqRecord = new LoginReqRecord("hola@hotmail.com", "12");

        when(service.registro(any()))
                .thenReturn(loginResRecord);

        mockMvc.perform(post("/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(loginReqRecord))
        ).andExpect(status().is(400));
    }

    @Test
    public void dadoUsuarioValidoEnAcceso_returnExitoso() throws Exception {
        LoginReqRecord loginReqRecord = new LoginReqRecord("hola@hotmail.com", "12345");

        when(service.registro(any()))
                .thenReturn(loginResRecord);

        mockMvc.perform(post("/auth/acceder")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(loginReqRecord))
        ).andExpect(status().is(200));
    }

    @Test
    public void dadoUsuarioNoValidoEnAcceso_returnExitoso() throws Exception {
        LoginReqRecord loginReqRecord = new LoginReqRecord("hola@hotmail.com", "12");

        when(service.registro(any()))
                .thenReturn(loginResRecord);

        mockMvc.perform(post("/auth/acceder")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(loginReqRecord))
        ).andExpect(status().is(400));
    }

    @Test
    public void dadoTokenValido_returnExitoso() throws Exception {
        ValidarTokenReqRecord validarTokenReqRecord = new ValidarTokenReqRecord("token");

        when(service.registro(any()))
                .thenReturn(loginResRecord);

        mockMvc.perform(post("/auth/validar")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(validarTokenReqRecord))
        ).andExpect(status().is(200));
    }

    @Test
    public void dadoTokenNoValido_returnExitoso() throws Exception {
        ValidarTokenReqRecord validarTokenReqRecord = new ValidarTokenReqRecord("");

        when(service.registro(any()))
                .thenReturn(loginResRecord);

        mockMvc.perform(post("/auth/validar")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(validarTokenReqRecord))
        ).andExpect(status().is(400));
    }



}
