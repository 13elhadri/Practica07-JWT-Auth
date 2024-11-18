package org.example.practica1.websocket.notifications.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.websocket.notifications.dto.FunkoNotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FunkoNotificationMapperTest {

    @Autowired
    private FunkoNotificationMapper funkoNotificationMapper;

    private Categoria categoriaTest;
    private Descripcion descripcionTest;

    @BeforeEach
    void setUp() {
        // Crear una categoría de prueba
        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();

        // Crear una descripción de prueba
        descripcionTest = new Descripcion("Test Description");
    }

    @Test
    void toProductNotificationDto() {
        // Crear un objeto Funko de prueba
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko Test")
                .precio(20)
                .categoria(categoriaTest)
                .descripcion(descripcionTest)
                .imagen("test.jpg")
                .createdAt(LocalDateTime.of(2023, 1, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 1, 2, 12, 0))
                .build();

        // Llamar al método toProductNotificationDto y verificar el resultado
        FunkoNotificationResponse response = funkoNotificationMapper.toProductNotificationDto(funko);

        // Verificar que todos los valores del FunkoNotificationResponse sean correctos
        assertNotNull(response);
        assertEquals(funko.getId(), response.id());
        assertEquals(funko.getNombre(), response.nombre());
        assertEquals(funko.getPrecio(), response.precio());
        assertEquals(funko.getCategoria().getNombre().name(), response.categoria());
        assertEquals(funko.getDescripcion().getDescripcion(), response.descripcion());
        assertEquals(funko.getImagen(), response.imagen());
        assertEquals(funko.getCreatedAt().toString(), response.createdAt());
        assertEquals(funko.getUpdatedAt().toString(), response.updatedAt());
    }
}
