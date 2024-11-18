package org.example.practica1.categoria.service;

import static org.junit.jupiter.api.Assertions.*;


import org.example.practica1.categoria.exceptions.CategoriaExists;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaServiceImpl service;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoriaTest));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var result = service.getAll(Optional.empty(),Optional.empty(),pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getNombre()),
                () -> assertEquals(categoriaTest.getId(), result.getContent().get(0).getId())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllNombre() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoriaTest));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // Test with 'nombre' specified, 'isDeleted' empty
        var result = service.getAll(Optional.of("DISNEY"), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getNombre()),
                () -> assertEquals(categoriaTest.getId(), result.getContent().get(0).getId())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllIsDeleted() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoriaTest));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // Test with 'isDeleted' specified, 'nombre' empty
        var result = service.getAll(Optional.empty(), Optional.of(false), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getNombre()),
                () -> assertEquals(categoriaTest.getId(), result.getContent().get(0).getId())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllNombreIsDeleted() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoriaTest));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // Test with both 'nombre' and 'isDeleted' specified
        var result = service.getAll(Optional.of("DISNEY"), Optional.of(false), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getNombre()),
                () -> assertEquals(categoriaTest.getId(), result.getContent().get(0).getId())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }


    @Test
    void getById() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));

        var result = service.getById(categoriaTest.getId());

        assertAll(
                () -> assertEquals(categoriaTest.getId(), result.getId()),
                () -> assertEquals(categoriaTest.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).findById(categoriaTest.getId());
    }

    @Test
    void getByIdNotFoundException() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.empty());

        var result = assertThrows(CategoriaNotFound.class, () -> service.getById(categoriaTest.getId()));

        assertEquals("Categoria con " + categoriaTest.getId() + " no encontrada", result.getMessage());

        verify(repository, times(1)).findById(categoriaTest.getId());
    }

    @Test
    void create() {
        when(repository.findByNombre(categoriaTest.getNombre())).thenReturn(Optional.empty());
        when(repository.save(categoriaTest)).thenReturn(categoriaTest);

        var result = service.create(categoriaTest);

        assertAll(
                () -> assertEquals(categoriaTest.getId(), result.getId()),
                () -> assertEquals(categoriaTest.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).findByNombre(categoriaTest.getNombre());
        verify(repository, times(1)).save(categoriaTest);
    }

    @Test
    void createCategoriaExistsException() {
        when(repository.findByNombre(categoriaTest.getNombre())).thenReturn(Optional.of(categoriaTest));

        var result = assertThrows(CategoriaExists.class, () -> service.create(categoriaTest));

        assertEquals("Categoria con " + categoriaTest.getNombre() + " ya existente", result.getMessage());

        verify(repository, times(1)).findByNombre(categoriaTest.getNombre());
        verify(repository, times(0)).save(any());
    }

    @Test
    void update() {
        Categoria updatedCategoria = Categoria.builder()
                .id(categoriaTest.getId())
                .nombre(Categoria.Nombre.SUPERHEROES)
                .isDeleted(false)
                .build();

        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));
        when(repository.findByNombre(updatedCategoria.getNombre())).thenReturn(Optional.empty());
        when(repository.save(any(Categoria.class))).thenReturn(updatedCategoria);

        var result = service.update(categoriaTest.getId(), updatedCategoria);

        assertAll(
                () -> assertEquals(updatedCategoria.getId(), result.getId()),
                () -> assertEquals(updatedCategoria.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(1)).findByNombre(updatedCategoria.getNombre());
        verify(repository, times(1)).save(any(Categoria.class));
    }

    @Test
    void updateNotFoundException() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.empty());

        var result = assertThrows(CategoriaNotFound.class, () -> service.update(categoriaTest.getId(), categoriaTest));

        assertEquals("Categoria con " + categoriaTest.getId() + " no encontrada", result.getMessage());

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(0)).save(any());
    }
    @Test
    void updateCategoriaExists() {
        Categoria updatedCategoria = Categoria.builder()
                .id(categoriaTest.getId())
                .nombre(Categoria.Nombre.SUPERHEROES)
                .isDeleted(false)
                .build();

        // Simulamos que ya existe una categoría con el nombre 'SUPERHEROES'
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));
        when(repository.findByNombre(updatedCategoria.getNombre())).thenReturn(Optional.of(updatedCategoria));

        assertThrows(CategoriaExists.class, () -> {
            service.update(categoriaTest.getId(), updatedCategoria);
        });

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(1)).findByNombre(updatedCategoria.getNombre());
        verify(repository, times(0)).save(any(Categoria.class)); // No se debe intentar guardar
    }

    @Test
    void delete() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));
        doNothing().when(repository).deleteById(categoriaTest.getId());

        service.delete(categoriaTest.getId());

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(1)).deleteById(categoriaTest.getId());
    }

    @Test
    void deleteNotFoundException() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.empty());

        var result = assertThrows(CategoriaNotFound.class, () -> service.delete(categoriaTest.getId()));

        assertEquals("Categoria con " + categoriaTest.getId() + " no encontrada", result.getMessage());

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(0)).deleteById(any());
    }
}
