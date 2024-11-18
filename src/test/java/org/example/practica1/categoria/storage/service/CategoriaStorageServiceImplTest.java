package org.example.practica1.categoria.storage.service;

import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CategoriaStorageServiceImplTest {

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaMapper categoriaMapper;

    @Autowired
    private CategoriaStorageService storageService;

    @Value("${upload-jsons.root-location.read}")
    private String readPath;

    @Value("${upload-jsons.root-location.write}")
    private String writePath;

    @Test
    public void testInitDirectories() {
        storageService.init();
        assertTrue(Files.exists(Paths.get(readPath)));
        assertTrue(Files.exists(Paths.get(writePath)));
    }

    @Test
    public void testStoreFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "categorias.json",
                "application/json",
                "[{\"nombre\": \"PELICULA\"}]".getBytes()
        );
        when(categoriaService.create(any(Categoria.class))).thenReturn(null);
        String storedFilename = storageService.store(file);
        assertNotNull(storedFilename);
        assertTrue(storedFilename.contains("categorias"));
    }

    @Test
    public void testReadJsonAndCreateCategories() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "categorias.json",
                "application/json",
                "[{\"nombre\": \"PELICULA\"}]".getBytes()
        );
        Categoria categoria = new Categoria(UUID.randomUUID(), Categoria.Nombre.PELICULA, null, null, false);
        when(categoriaService.create(any(Categoria.class))).thenReturn(categoria);
        List<Categoria> categorias = storageService.readJson(file);
        assertNotNull(categorias);
        assertEquals(1, categorias.size());
        assertEquals("PELICULA", categorias.get(0).getNombre().name());
    }

//
    @Test
    public void testLoadFile() throws IOException {
        // Crear el archivo de prueba
        Path filePath = Paths.get(writePath, "test.json");
        Files.createFile(filePath);

        try {
            // Realizar las verificaciones
            Path loadedFile = storageService.load("test.json");
            assertNotNull(loadedFile);
            assertTrue(Files.exists(loadedFile));
        } finally {
            // Eliminar el archivo después de la prueba
            Files.deleteIfExists(filePath);
        }
    }


    @Test
    public void testDeleteAllFiles() {
        storageService.deleteAll();
        assertFalse(Files.exists(Paths.get(readPath)));
    }


}

