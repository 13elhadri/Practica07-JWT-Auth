package org.example.practica1.storage.exceptions;

import java.io.Serial;

public abstract class StorageExceptions extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public StorageExceptions(String mensaje) {
        super(mensaje);
    }
}