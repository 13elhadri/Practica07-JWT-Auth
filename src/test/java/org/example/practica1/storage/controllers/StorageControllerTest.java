package org.example.practica1.storage.controllers;

import org.example.practica1.storage.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class StorageControllerTest {

    private final String apiVersion = "/api/v1/storage"; // Asegúrate de que este valor coincide con tu configuración
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StorageService storageService;

    @Test
    void serveFile() throws Exception {
        String filename = "testfile.png";
        Resource file = mock(Resource.class);
        when(storageService.loadAsResource(filename)).thenReturn(file);
        when(file.getFile()).thenReturn(new File("path/to/testfile.png"));
        when(file.getFilename()).thenReturn(filename);

        MockHttpServletResponse response = mockMvc.perform(
                        get(apiVersion+"/" + filename)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        verify(storageService, times(1)).loadAsResource(filename);
    }
}