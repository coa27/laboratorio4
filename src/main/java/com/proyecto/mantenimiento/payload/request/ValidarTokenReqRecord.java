package com.proyecto.mantenimiento.payload.request;

import jakarta.validation.constraints.NotBlank;

public record ValidarTokenReqRecord(@NotBlank String token) {
}
