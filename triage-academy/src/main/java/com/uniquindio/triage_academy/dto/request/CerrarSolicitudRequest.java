package com.uniquindio.triage_academy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CerrarSolicitudRequest {
    @NotBlank
    private String observacionCierre;
}
