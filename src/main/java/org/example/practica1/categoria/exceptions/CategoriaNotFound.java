package org.example.practica1.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaNotFound extends CategoriaExceptions {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public CategoriaNotFound(UUID id) {
        super("Categoria con " +id+" no encontrada");
    }
}
