package org.example.practica1.categoria.service;

import org.example.practica1.categoria.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaService {

    Page<Categoria> getAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Categoria getById(UUID id);

    Categoria create(Categoria categoria);

    Categoria update(UUID id, Categoria categoria);

    void delete(UUID id);
}
