package org.example.practica1.funko.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FunkoDto(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,
        @Min(value = 1, message = "El precio debe ser mayor que 10")
        @Max(value = 100, message = "El precio debe ser menor que 100")
        int precio,
        @NotNull(message = "La categoría no puede estar vacía")
        UUID categoriaId,
        @NotNull(message = "La descripcion no puede estar vacía")
        String descripcion,
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,
        String imagen

){}