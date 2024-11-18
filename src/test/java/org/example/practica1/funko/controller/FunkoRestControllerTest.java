package org.example.practica1.funko.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.exceptions.FunkoNotFound;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.services.FunkosService;
import org.example.practica1.pagination.PageResponse;
import org.example.practica1.pagination.PaginationLinksUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class FunkoRestControllerTest {

    private final String endpoint = "/api/v1/funkos";

    @Autowired
    MockMvc mockMvc;
    @MockBean
    FunkosService service;
    @MockBean
    PaginationLinksUtils paginationLinksUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public FunkoRestControllerTest(FunkosService service, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.paginationLinksUtils = paginationLinksUtils;
        mapper.registerModule(new JavaTimeModule());
    }
    private Funko funkoTest;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {

        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();

        funkoTest = Funko.builder()
                .id(1L)
                .nombre("Funko Test")
                .precio(15)
                .categoria(categoriaTest)
                .descripcion(new Descripcion("Test Description"))
                .imagen("test.jpg")
                .build();
    }

    @Test
    void getFunkos() throws Exception {
        var funkoList = List.of(funkoTest);
        Page<Funko> page = new PageImpl<>(funkoList);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals(funkoTest.getNombre(), res.content().get(0).getNombre()),
                () -> assertEquals(funkoTest.getId(), res.content().get(0).getId())
        );

        verify(service, times(1)).getAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getFunkosOptionalNoEmpty() throws Exception {
        String nombre = "Funko Test";
        Integer precio = 15;
        String categoria = "DISNEY";

        var funkoList = List.of(funkoTest);
        Page<Funko> page = new PageImpl<>(funkoList);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(service.getAll(Optional.of(nombre), Optional.of(precio), Optional.of(categoria), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .param("nombre", nombre)
                                .param("precio", String.valueOf(precio))
                                .param("categoria", categoria)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Funko> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals(funkoTest.getNombre(), res.content().get(0).getNombre()),
                () -> assertEquals(funkoTest.getId(), res.content().get(0).getId())
        );

        verify(service, times(1)).getAll(Optional.of(nombre), Optional.of(precio), Optional.of(categoria), pageable);
    }



    @Test
    void getFunko() throws Exception {
        when(service.getById(1L)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint+"/" + 1)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertEquals(200, response.getStatus());
        assertEquals(funkoTest.getId(), res.getId());
        assertEquals(funkoTest.getNombre(), res.getNombre());

        verify(service, times(1)).getById(1L);
    }

    @Test
    void getFunkonotFound() throws Exception {
        Long id = 1L;

        when(service.getById(id)).thenThrow(new FunkoNotFound(id));

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + id)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).getById(id);
    }

    @Test
    void createFunko() throws Exception {
        FunkoDto funkoDto1 = new FunkoDto("Funko Test", 15,UUID.randomUUID() , "Test Description", 10,"imagen.png");

        when(service.create(funkoDto1)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertEquals(201, response.getStatus());
        assertEquals(funkoTest.getId(), res.getId());
        assertEquals(funkoTest.getNombre(), res.getNombre());

        verify(service, times(1)).create(funkoDto1);
    }

    @Test
    void createFunkocategoriaNotFound() throws Exception {
        FunkoDto funkoDto1 = new FunkoDto("Funko Test", 15, UUID.randomUUID(), "Test Description", 10,"imagen.png");

        when(service.create(funkoDto1)).thenThrow(new CategoriaNotFound(funkoDto1.categoriaId()));

        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).create(funkoDto1);
    }




/*

    @Test
    void nuevoFunko() throws Exception {
        Long id = 1L;
        MultipartFile newImage = mock(MultipartFile.class);
        when(newImage.isEmpty()).thenReturn(false);
        when(service.updateImage(id,newImage)).thenReturn(funkoTest);


        MockHttpServletResponse response = mockMvc.perform(
                        patch(endpoint+"/imagen/"+1)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .param("file", "newImage.jpg")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertEquals(200, response.getStatus());
        assertEquals("newImage.jpg", res.getImagen());
    }
    */


    @Test
    void updateFunko() throws Exception {
        Long id = 1L;
        FunkoDto funkoDto = new FunkoDto("Funko Test", 15, categoriaTest.getId(), "DISNEY",10, funkoTest.getImagen());

        Funko funkoUpdated = new Funko(id, "Funko Test", 15,
                new Categoria(UUID.randomUUID(), Categoria.Nombre.DISNEY, LocalDateTime.now(), LocalDateTime.now(), false),
                new Descripcion("Test Description"), "image.jpg", 10,LocalDateTime.now(), LocalDateTime.now());

        when(service.update(id, funkoDto)).thenReturn(funkoUpdated);

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint+"/"+1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertEquals(200, response.getStatus());
        assertEquals(funkoUpdated.getNombre(), res.getNombre());
        assertEquals(funkoUpdated.getPrecio(), res.getPrecio());
        assertEquals(funkoUpdated.getDescripcion(), res.getDescripcion());
        verify(service, times(1)).update(id, funkoDto);
    }

    @Test
    void updateFunkocategoriaNotFound() throws Exception {
        Long id = 1L;
        FunkoDto funkoDto = new FunkoDto("Funko Test", 15, UUID.randomUUID(), "DISNEY", 10,funkoTest.getImagen());

        when(service.update(id, funkoDto)).thenThrow(new CategoriaNotFound(funkoDto.categoriaId()));

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).update(id, funkoDto);
    }

    @Test
    void updateFunkofunkoNotFound() throws Exception {
        Long id = 1L;
        FunkoDto funkoDto = new FunkoDto("Funko Test", 15, categoriaTest.getId(), "DISNEY", 10,funkoTest.getImagen());

        when(service.update(id, funkoDto)).thenThrow(new FunkoNotFound(id));

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).update(id, funkoDto);
    }



    @Test
    void deleteFunko() throws Exception {
        Long id = 1L;

        when(service.delete(id)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(endpoint+"/"+1)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());
        verify(service, times(1)).delete(id);
    }

    @Test
    void deleteFunkofunkoNotFound() throws Exception {
        Long id = 1L;

        when(service.delete(id)).thenThrow(new FunkoNotFound(id));

        MockHttpServletResponse response = mockMvc.perform(
                        delete(endpoint + "/" + 1)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).delete(id);
    }

    @Test
    void handleValidation() throws Exception {
        FunkoDto funkoDto = new FunkoDto(
                "",
                15,
                UUID.randomUUID(),
                "Descripción de prueba",
                10,
                "imagen.jpg"
        );

        mockMvc.perform(post("/api/v1/funkos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(funkoDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacío"));
    }






}