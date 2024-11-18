package org.example.practica1.websocket.notifications.dto;

public record FunkoNotificationResponse(
        Long id,
        String nombre,
        int precio,
        String categoria,
        String descripcion,
        String imagen,
        String createdAt,
        String updatedAt
) {
}