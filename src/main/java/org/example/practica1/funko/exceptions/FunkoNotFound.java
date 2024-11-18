package org.example.practica1.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFound extends FunkoExceptions{
    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public FunkoNotFound(Long id){super("Funko con " +id+" no encontrado");}

}
