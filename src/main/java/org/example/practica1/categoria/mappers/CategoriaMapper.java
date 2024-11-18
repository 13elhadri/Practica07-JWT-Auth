package org.example.practica1.categoria.mappers;


import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.models.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria fromDto(CategoriaDto categoriaDto) {
        var categoria = new Categoria();


        categoria.setNombre(Categoria.Nombre.valueOf(categoriaDto.nombre()));

        categoria.setIsDeleted(categoriaDto.isDeleted());

        return categoria;
    }

    public CategoriaDto toDto(Categoria categoria) {
        return new CategoriaDto(
                categoria.getNombre().name(),
                categoria.getIsDeleted()
        );
    }
}
