package com.proyecto.mantenimiento.exceptions.customs;

import com.proyecto.mantenimiento.services.impl.UsuariosServiceImpl;

public class UsuarioEnUsoException extends RuntimeException{

    public UsuarioEnUsoException(String message){ super(message);}

}
