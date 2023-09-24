package com.proyecto.mantenimiento.payload.request;

import jakarta.validation.constraints.Size;

public record LoginReqRecord(String email,
                             @Size(min = 4, max = 18, message = "La contrasenia debe tener entre 4 a 18 caracteres")
                             String password) {
}
