package com.proyecto.mantenimiento.exceptions.customs;

public class CredencialesErroneas extends RuntimeException{

    public CredencialesErroneas(String mensaje) {
        super(mensaje);
    }
}
