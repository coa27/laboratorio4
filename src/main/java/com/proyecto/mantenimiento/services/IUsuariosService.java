package com.proyecto.mantenimiento.services;

import com.proyecto.mantenimiento.payload.request.LoginReqRecord;
import com.proyecto.mantenimiento.payload.request.ValidarTokenReqRecord;
import com.proyecto.mantenimiento.payload.response.LoginResRecord;
import org.springframework.stereotype.Service;

public interface IUsuariosService {

    LoginResRecord acceder(LoginReqRecord loginReqRecordA);
    LoginResRecord registro(LoginReqRecord registroReqRecord);
    LoginResRecord validarToken(ValidarTokenReqRecord token);

}
