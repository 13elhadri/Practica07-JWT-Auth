package org.example.practica1.categoria.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.example.practica1.categoria.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();

        categoriaRepository.save(categoriaTest);
    }

    @Test
    void findByNombreExists() {

        Optional<Categoria> result = categoriaRepository.findByNombre(Categoria.Nombre.DISNEY);

        assertTrue(result.isPresent());
    }

    @Test
    void findByNombreNotExists() {
        Optional<Categoria> result = categoriaRepository.findByNombre(Categoria.Nombre.SUPERHEROES);

        assertFalse(result.isPresent());
    }
}
