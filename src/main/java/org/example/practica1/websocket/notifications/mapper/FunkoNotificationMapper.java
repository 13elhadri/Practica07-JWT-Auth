package org.example.practica1.websocket.notifications.mapper;


import org.example.practica1.funko.models.Funko;
import org.example.practica1.websocket.notifications.dto.FunkoNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationResponse toProductNotificationDto(Funko funko) {
        return new FunkoNotificationResponse(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCategoria().getNombre().name(),
                funko.getDescripcion().getDescripcion(),
                funko.getImagen(),
                funko.getCreatedAt().toString(),
                funko.getUpdatedAt().toString()
        );
    }
}
