package org.example.practica1.categoria.mappers;

import static org.junit.jupiter.api.Assertions.*;

import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.models.Categoria;
import org.junit.jupiter.api.Test;

public class CategoriaMapperTest {

    private final CategoriaMapper categoriaMapper = new CategoriaMapper();

    @Test
    public void testFromDto() {
        CategoriaDto categoriaDto = new CategoriaDto("SERIE", false);
        Categoria categoria = categoriaMapper.fromDto(categoriaDto);
        assertEquals(Categoria.Nombre.SERIE, categoria.getNombre());
        assertFalse(categoria.getIsDeleted());
    }

    @Test
    public void testToDto() {
        Categoria categoria = new Categoria();
        categoria.setNombre(Categoria.Nombre.SERIE);
        categoria.setIsDeleted(false);
        CategoriaDto categoriaDto = categoriaMapper.toDto(categoria);
        assertEquals("SERIE", categoriaDto.nombre());
        assertFalse(categoriaDto.isDeleted());
    }
}

