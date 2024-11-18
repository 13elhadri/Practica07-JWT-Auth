package org.example.practica1.funko.mappers;

import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.models.Funko;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public Funko fromDto(FunkoDto funkoDto, Categoria categoria) {
        String imagen = (funkoDto.imagen() == null) ? Funko.IMAGE_DEFAULT : funkoDto.imagen();

        return Funko.builder()
                .nombre(funkoDto.nombre())
                .precio(funkoDto.precio())
                .categoria(categoria)
                .descripcion(new Descripcion(funkoDto.descripcion()))
                .imagen(imagen)
                .build();
    }

    public FunkoDto toDto(Funko funko) {
        return new FunkoDto(
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCategoria().getId(),
                funko.getDescripcion().getDescripcion(),
                funko.getStock(),
                funko.getImagen()
        );
    }

}
