package org.example.practica1.categoria.repository;

import org.example.practica1.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID>, JpaSpecificationExecutor<Categoria> {
    Optional<Categoria> findByNombre(Categoria.Nombre nombre);
}
