package org.example.practica1.categoria.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaDto (
        @NotBlank(message = "El nombre no puede estar vacio")
        String nombre,

        Boolean isDeleted
){ }
