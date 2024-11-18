package org.example.practica1.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StorageBadRequest extends StorageExceptions {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public StorageBadRequest(String mensaje) {
        super(mensaje);
    }
}