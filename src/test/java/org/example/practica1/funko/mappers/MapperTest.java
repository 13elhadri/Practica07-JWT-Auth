package org.example.practica1.funko.mappers;

import static org.junit.jupiter.api.Assertions.*;


import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class MapperTest {

    private Mapper mapper;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        mapper = new Mapper();

        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();
    }

    @Test
    void fromDto() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, categoriaTest.getId(), "Test Description", 10,"test.jpg");

        Funko funko = mapper.fromDto(funkoDto, categoriaTest);


        assertNotNull(funko);
        assertEquals(funkoDto.nombre(), funko.getNombre());
        assertEquals(funkoDto.precio(), funko.getPrecio());
        assertEquals(categoriaTest, funko.getCategoria());
        assertEquals("Test Description", funko.getDescripcion().getDescripcion());
        assertEquals("test.jpg", funko.getImagen());
    }

    @Test
    void fromDtoImageNull() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, categoriaTest.getId(), "Test Description", 10,null);

        Funko funko = mapper.fromDto(funkoDto, categoriaTest);

        assertNotNull(funko);
        assertEquals(Funko.IMAGE_DEFAULT, funko.getImagen());  // Verifica que se use la imagen por defecto
    }


    @Test
    void toDto() {
        Funko funko = Funko.builder()
                .nombre("Funko Test")
                .precio(20)
                .categoria(categoriaTest)
                .descripcion(new Descripcion("Test Description"))
                .imagen("test.jpg")
                .stock(10)
                .build();

        FunkoDto funkoDto = mapper.toDto(funko);

        assertNotNull(funkoDto);
        assertEquals(funko.getNombre(), funkoDto.nombre());
        assertEquals(funko.getPrecio(), funkoDto.precio());
        assertEquals(funko.getCategoria().getId(), funkoDto.categoriaId());
        assertEquals(funko.getDescripcion().getDescripcion(), funkoDto.descripcion());
        assertEquals(funko.getStock(), funkoDto.stock());
        assertEquals(funko.getImagen(), funkoDto.imagen());
    }




}
