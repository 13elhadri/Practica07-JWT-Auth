package org.example.practica1.categoria.storage.controller;

import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.example.practica1.categoria.storage.service.CategoriaStorageService;
import org.example.practica1.categoria.storage.service.CategoriaStorageServiceImpl;
import org.example.practica1.storage.exceptions.StorageBadRequest;
import org.example.practica1.storage.exceptions.StorageInternal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoriaFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaStorageService storageService;


    @Test
    public void testUploadFileBadRequest() throws Exception {
        // Crea un archivo vacío para provocar un error
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.json",
                "application/json",
                "".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/categorias/ficheros")
                        .file(file)
                        .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testServeDefaultFile() throws Exception {
        // Simulamos que el archivo existe en el servicio
        String filename = "default.json";
        Resource resource = mock(Resource.class);
        when(storageService.loadAsResource(filename)).thenReturn(resource);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categorias/ficheros/" + filename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""));
    }
}


