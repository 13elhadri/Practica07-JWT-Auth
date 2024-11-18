package org.example.practica1.categoria.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.exceptions.CategoriaExists;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.example.practica1.pagination.PageResponse;
import org.example.practica1.pagination.PaginationLinksUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

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
class CategoriaRestControllerTest {
    private final String endpoint = "/api/v1/categorias";

    @Autowired
    MockMvc mockMvc;

    private final UUID id = UUID.randomUUID();
    Categoria categoriaTest = Categoria.builder()
            .id(id)
            .nombre(Categoria.Nombre.SUPERHEROES)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

    @MockBean
    CategoriaService service;
    @MockBean
    CategoriaMapper categoriaMapper;
    @MockBean
    PaginationLinksUtils paginationLinksUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JacksonTester<CategoriaDto> categoriaDto;

    @Autowired
    public CategoriaRestControllerTest(CategoriaService service, CategoriaMapper categoriaMapper, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.categoriaMapper = categoriaMapper;
        this.paginationLinksUtils = paginationLinksUtils;
        mapper.registerModule(new JavaTimeModule());
    }



    @Test
    void getCategorias() throws Exception {
        var lista= List.of(categoriaTest);

        Page<Categoria> page = new PageImpl<>(lista);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(service.getAll(Optional.empty(),Optional.empty(),pageable)).thenReturn(page);

        MockHttpServletResponse response =mockMvc.perform(
                        get(endpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){});


        assertEquals(200,response.getStatus());

        verify(service,times(1)).getAll(Optional.empty(),Optional.empty(),pageable);
    }

    @Test
    void getCategoriasNombreDeleted() throws Exception {

        var lista = List.of(categoriaTest);

        Page<Categoria> page = new PageImpl<>(lista);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(service.getAll(Optional.of("SUPERHEROES"), Optional.of(false), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .param("nombre", "SUPERHEROES")
                                .param("isDeleted", "false")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertEquals(200, response.getStatus());

        assertEquals(1, res.content().size());
        assertEquals("SUPERHEROES", res.content().get(0).getNombre().name());
        assertFalse(res.content().get(0).getIsDeleted());

        verify(service, times(1)).getAll(Optional.of("SUPERHEROES"), Optional.of(false), pageable);
    }

    @Test
    void getCategoria() throws Exception {
        when(service.getById(id)).thenReturn(categoriaTest);

        MockHttpServletResponse response =mockMvc.perform(
                        get(endpoint+"/"+id)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(),Categoria.class);

        assertEquals(200,response.getStatus());

        verify(service,times(1)).getById(id);

    }

    @Test
    void getCategoriaNotFound() throws Exception {
        UUID idNotFound = UUID.randomUUID();

        when(service.getById(idNotFound)).thenThrow(new CategoriaNotFound(idNotFound));

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + idNotFound)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).getById(idNotFound);
    }

    @Test
    void createCategoria() throws Exception {

       CategoriaDto categoriaDto1 = new CategoriaDto("OTROS", false);

       when(categoriaMapper.fromDto(categoriaDto1)).thenReturn(categoriaTest);
        when(service.create(categoriaTest)).thenReturn(categoriaTest);

        MockHttpServletResponse response =mockMvc.perform(
                       post(endpoint)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(categoriaDto1))
                               .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(),Categoria.class);

        assertEquals(201,response.getStatus());

        verify(service,times(1)).create(categoriaTest);
    }

    @Test
    void createCategoriaExists() throws Exception {
        CategoriaDto categoriaDto1 = new CategoriaDto("OTROS", false);

        when(categoriaMapper.fromDto(categoriaDto1)).thenReturn(categoriaTest);
        when(service.create(categoriaTest)).thenThrow(new CategoriaExists(categoriaTest.getNombre()));

        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).create(categoriaTest);
    }

    @Test
    void updateCategoria() throws Exception {
        CategoriaDto categoriaDto1 = new CategoriaDto("OTROS", false);

        Categoria categoriaUpdated = Categoria.builder()
                .id(id)
                .nombre(Categoria.Nombre.OTROS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(categoriaMapper.fromDto(categoriaDto1)).thenReturn(categoriaUpdated);
        when(service.update(id,categoriaUpdated)).thenReturn(categoriaUpdated);

        MockHttpServletResponse response =mockMvc.perform(
                        put(endpoint+"/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(),Categoria.class);

        assertEquals(200,response.getStatus());

        verify(service,times(1)).update(id,categoriaUpdated);
    }

    @Test
    void updateCategoriaNotFound() throws Exception {
        CategoriaDto categoriaDto1 = new CategoriaDto("OTROS", false);

        Categoria categoriaUpdated = Categoria.builder()
                .id(id)
                .nombre(Categoria.Nombre.OTROS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        UUID idNotFound = UUID.randomUUID();

        when(categoriaMapper.fromDto(categoriaDto1)).thenReturn(categoriaUpdated);
        when(service.update(idNotFound,categoriaUpdated)).thenThrow(new CategoriaNotFound(idNotFound));

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/" + idNotFound)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).update(idNotFound, categoriaUpdated);
    }

    @Test
    void updateCategoriaExists() throws Exception {
        CategoriaDto categoriaDto1 = new CategoriaDto("OTROS", false);

        UUID id = UUID.randomUUID();

        Categoria categoriaExistente = Categoria.builder()
                .id(id)
                .nombre(Categoria.Nombre.OTROS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(categoriaMapper.fromDto(categoriaDto1)).thenReturn(categoriaExistente);
        when(service.update(id, categoriaExistente)).thenThrow(new CategoriaExists(categoriaExistente.getNombre()));

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).update(id, categoriaExistente);
    }



    @Test
    void deleteCategoria() throws Exception{

        doNothing().when(service).delete(id);

        MockHttpServletResponse response =mockMvc.perform(
                        delete(endpoint+"/"+id)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();



        assertEquals(204,response.getStatus());

        verify(service,times(1)).delete(id);


    }

    @Test
    void deleteCategoriaNotFound() throws Exception {
        UUID idNotFound = UUID.randomUUID();

        doThrow(new CategoriaNotFound(idNotFound)).when(service).delete(idNotFound);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(endpoint + "/" + idNotFound)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(service, times(1)).delete(idNotFound);
    }

    @Test
    void validationFail() throws Exception {
        // Simulamos una solicitud POST con un DTO de Categoria inválido (por ejemplo, nombre vacío)
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\", \"isDeleted\": false }"))  // Nombre vacío, que debería fallar la validación
                .andExpect(status().isBadRequest())  // Verificamos que el estado HTTP sea 400 BAD_REQUEST
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacio"));  // Verificamos que el mensaje de error para "nombre" sea el esperado
    }

    @Test
    void missingRequiredField() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"isDeleted\": false }"))  // Falta el campo nombre
                .andExpect(status().isBadRequest())  // Verificamos que el estado HTTP sea 400 BAD_REQUEST
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacio"));  // Verificamos el mensaje de error para "nombre"
    }

}