package com.proyecto.mantenimiento.exceptions.customs;

public class UsuarioNoEncontradoException extends RuntimeException{

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }

}
