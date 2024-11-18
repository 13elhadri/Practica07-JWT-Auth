package org.example.practica1.funko.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.exceptions.FunkoNotFound;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.repository.FunkoRepository;
import org.example.practica1.storage.service.StorageService;
import org.example.practica1.websocket.config.WebSocketConfig;
import org.example.practica1.websocket.config.WebSocketHandler;
import org.example.practica1.websocket.notifications.mapper.FunkoNotificationMapper;
import org.example.practica1.websocket.notifications.models.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkosServiceImplTest {

    @Mock
    private FunkoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private Mapper mapper;

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private FunkoNotificationMapper funkoNotificationMapper;

    WebSocketHandler webSocketHandlerMock = mock(WebSocketHandler.class);

    @InjectMocks
    private FunkosServiceImpl service;

    private Funko funkoTest;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        service.setWebSocketService(webSocketHandlerMock);

        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();

        funkoTest = Funko.builder()
                .id(1L)
                .nombre("Funko Test")
                .precio(15)
                .stock(10)
                .categoria(categoriaTest)
                .descripcion(new Descripcion("Test Description"))
                .imagen("test.jpg")
                .build();
    }


    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Funko> expectedPage = new PageImpl<>(List.of(funkoTest));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var result = service.getAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Funko Test", result.getContent().get(0).getNombre()),
                () -> assertEquals(15, result.getContent().get(0).getPrecio()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getCategoria().getNombre())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllNombre() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Funko funkoTest = Funko.builder()
                .nombre("Funko Test")
                .precio(15)
                .categoria(categoriaTest)
                .build();
        Page<Funko> expectedPage = new PageImpl<>(List.of(funkoTest));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var result = service.getAll(Optional.of("Funko Test"), Optional.empty(), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Funko Test", result.getContent().get(0).getNombre())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllPrecio() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Funko funkoTest = Funko.builder()
                .nombre("Funko Test")
                .precio(15)
                .categoria(categoriaTest)
                .build();
        Page<Funko> expectedPage = new PageImpl<>(List.of(funkoTest));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var result = service.getAll(Optional.empty(), Optional.of(15), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(15, result.getContent().get(0).getPrecio())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllCategoria() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Funko funkoTest = Funko.builder()
                .nombre("Funko Test")
                .precio(15)
                .categoria(categoriaTest)
                .build();
        Page<Funko> expectedPage = new PageImpl<>(List.of(funkoTest));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var result = service.getAll(Optional.empty(), Optional.empty(), Optional.of("Categoria Test"), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getCategoria().getNombre())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Funko funkoTest = Funko.builder()
                .nombre("Funko Test")
                .precio(15)
                .categoria(categoriaTest)
                .build();
        Page<Funko> expectedPage = new PageImpl<>(List.of(funkoTest));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var result = service.getAll(Optional.of("Funko Test"), Optional.of(15), Optional.of("Categoria Test"), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Funko Test", result.getContent().get(0).getNombre()),
                () -> assertEquals(15, result.getContent().get(0).getPrecio()),
                () -> assertEquals(categoriaTest.getNombre(), result.getContent().get(0).getCategoria().getNombre())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }




    @Test
    void getById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(funkoTest));
        Funko result = service.getById(id);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals(funkoTest.getNombre(), result.getNombre())
        );
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getByIdNotFoundException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        var exception = assertThrows(FunkoNotFound.class, () -> service.getById(id));
        assertEquals("Funko con " + id + " no encontrado", exception.getMessage());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void create() throws IOException {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, categoriaTest.getId(), "Test Description", 10,"test.jpg");
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.of(categoriaTest));
        when(mapper.fromDto(funkoDto, categoriaTest)).thenReturn(funkoTest);
        when(repository.save(funkoTest)).thenReturn(funkoTest);
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        Funko result = service.create(funkoDto);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals(funkoDto.nombre(), result.getNombre()),
                () -> assertEquals(funkoDto.imagen(), result.getImagen())
        );
        verify(categoriaRepository, times(1)).findById(funkoDto.categoriaId());
        verify(repository, times(1)).save(funkoTest);
    }

    @Test
    void createCategoriaNotFound() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, UUID.randomUUID(), "Test Description", 10,"testImage.jpg");
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.empty());
        var exception = assertThrows(CategoriaNotFound.class, () -> service.create(funkoDto));
        assertEquals("Categoria con " + funkoDto.categoriaId() + " no encontrada", exception.getMessage());
        verify(repository, times(0)).save(any());
    }

    @Test
    void update() throws IOException {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 25, categoriaTest.getId(), "Updated Description", 10,"test.jpg");
        when(repository.findById(funkoTest.getId())).thenReturn(Optional.of(funkoTest));
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.of(categoriaTest));
        when(mapper.fromDto(funkoDto, categoriaTest)).thenReturn(funkoTest);
        when(repository.save(funkoTest)).thenReturn(funkoTest);
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        Funko result = service.update(funkoTest.getId(), funkoDto);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals(funkoDto.nombre(), result.getNombre()),
                () -> assertEquals(funkoDto.imagen(), result.getImagen())
        );
        verify(repository, times(1)).findById(funkoTest.getId());
        verify(categoriaRepository, times(1)).findById(funkoDto.categoriaId());
        verify(repository, times(1)).save(funkoTest);
    }

    @Test
    void updateCategoriaNotFound() {
        FunkoDto funkoDto = new FunkoDto("Funko Updated", 25, UUID.randomUUID(), "Updated Description", 10,"updatedImage.jpg");
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.empty());
        var exception = assertThrows(CategoriaNotFound.class, () -> service.update(funkoTest.getId(), funkoDto));
        assertEquals("Categoria con " + funkoDto.categoriaId() + " no encontrada", exception.getMessage());
        verify(repository, times(0)).save(any());
    }

/*
    @Test
    void updateImage() {
        Long id = funkoTest.getId();
        MultipartFile newImage = mock(MultipartFile.class);
        Funko funkoActualizado = new Funko(
                funkoTest.getId(),
                funkoTest.getNombre(),
                funkoTest.getPrecio(),
                funkoTest.getCategoria(),
                funkoTest.getDescripcion(),
                "newImage.jpg",
                funkoTest.getCreatedAt(),
                LocalDateTime.now()
        );


        when(repository.findById(id)).thenReturn(Optional.of(funkoTest));
        when(storageService.store(newImage)).thenReturn("newImage.jpg");
        when(repository.save(funkoActualizado)).thenReturn(funkoActualizado);


        Funko result = service.updateImage(id, newImage);
        System.out.println(result);

        assertAll(
                () -> assertEquals("newImage.jpg", result.getImagen()),  // Comprobamos que la imagen se haya actualizado
                () -> assertEquals(funkoTest.getId(), result.getId()),  // Verificamos que el id sigue siendo el mismo
                () -> assertEquals(funkoTest.getNombre(), result.getNombre())  // Verificamos que el nombre no cambió
        );

        verify(repository, times(1)).findById(id);
        verify(storageService, times(1)).store(newImage);
        //verify(repository, times(1)).save(funkoActualizado);
    }

 */



    @Test
    void delete() throws IOException {
        when(repository.findById(funkoTest.getId())).thenReturn(Optional.of(funkoTest));
        doNothing().when(repository).deleteById(funkoTest.getId());
        doNothing().when(storageService).delete(funkoTest.getImagen());
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        Funko result = service.delete(funkoTest.getId());
        assertEquals(funkoTest.getId(), result.getId());
        verify(repository, times(1)).findById(funkoTest.getId());
        verify(repository, times(1)).deleteById(funkoTest.getId());
        verify(storageService, times(1)).delete(funkoTest.getImagen());
    }

    @Test
    void deleteNotFound() {
        when(repository.findById(funkoTest.getId())).thenReturn(Optional.empty());
        var exception = assertThrows(FunkoNotFound.class, () -> service.delete(funkoTest.getId()));
        assertEquals("Funko con " + funkoTest.getId() + " no encontrado", exception.getMessage());
        verify(repository, times(1)).findById(funkoTest.getId());
        verify(repository, times(0)).deleteById(any());
        verify(storageService, times(0)).delete(any());
    }

    @Test
    void onChange() throws IOException {
        // Arrange
        doNothing().when(webSocketHandlerMock).sendMessage(any(String.class));

        // Act
        service.onChange(Notificacion.Tipo.CREATE, any(Funko.class));
    }

}

