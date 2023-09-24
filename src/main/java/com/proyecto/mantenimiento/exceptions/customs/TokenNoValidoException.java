package com.proyecto.mantenimiento.exceptions.customs;

public class TokenNoValidoException extends RuntimeException{

    public TokenNoValidoException(String mensaje){
        super(mensaje);
    }

}
