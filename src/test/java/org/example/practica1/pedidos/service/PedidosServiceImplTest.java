package org.example.practica1.pedidos.service;


import org.bson.types.ObjectId;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.services.FunkosService;
import org.example.practica1.pedidos.exceptions.PedidoNotFound;
import org.example.practica1.pedidos.exceptions.PedidoNotItems;
import org.example.practica1.pedidos.exceptions.ProductoBadPrice;
import org.example.practica1.pedidos.exceptions.ProductoNotStock;
import org.example.practica1.pedidos.models.LineaPedido;
import org.example.practica1.pedidos.models.Pedido;
import org.example.practica1.pedidos.repository.PedidosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidosServiceImplTest {
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private Mapper mapper;
    @Mock
    private FunkosService funkosService;
    @InjectMocks
    private PedidosServiceImpl pedidosService;

    private Funko funkoTest;
    private Categoria categoriaTest;
    private FunkoDto funkoDto;

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
                .stock(10)
                .categoria(categoriaTest)
                .descripcion(new Descripcion("Test Description"))
                .imagen("test.jpg")
                .build();

        funkoDto = new FunkoDto(
                "Funko Test",
                15,
                categoriaTest.getId(),
                "Test Description",
                10,
                "test.jpg"
        );

    }
    @Test
    public void findAll() {
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);
        when(pedidosRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Pedido> result = pedidosService.findAll(pageable);
        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );
        verify(pedidosRepository, times(1)).findAll(pageable);
    }

    @Test
    public void findById() {
        ObjectId idPedido = new ObjectId();
        Pedido expectedPedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(expectedPedido));
        Pedido result = pedidosService.findById(idPedido);
        assertEquals(expectedPedido, result);
        verify(pedidosRepository, times(1)).findById(idPedido);
    }
    @Test
    public void findByIdNotFound(){
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.findById(idPedido));
        verify(pedidosRepository, times(1)).findById(idPedido);
    }
    @Test
    public void findByIdUsuario() {
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);
        Long idUsuario = 1L;
        when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(expectedPage);
        Page<Pedido> result = pedidosService.findByIdUsuario(idUsuario, pageable);
        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );
        verify(pedidosRepository, times(1)).findByIdUsuario(idUsuario, pageable);
    }
    @Test
    void Save() {
        // Arrange
        Pedido pedido = new Pedido();
        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(15.00)
                .build();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToSave = new Pedido();
        pedidoToSave.setLineasPedido(List.of(lineaPedido));

        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToSave); // Utiliza any(Pedido.class) para cualquier instancia de Pedido
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);

        // Act
        Pedido resultPedido = pedidosService.save(pedido);

        // Assert
        assertAll(
                () -> assertEquals(pedidoToSave, resultPedido),
                () -> assertEquals(pedidoToSave.getLineasPedido(), resultPedido.getLineasPedido()),
                () -> assertEquals(pedidoToSave.getLineasPedido().size(), resultPedido.getLineasPedido().size())
        );

        // Verify
        verify(pedidosRepository).save(any(Pedido.class));
        verify(funkosService, times(2)).getById(anyLong());
    }
    @Test
    void saveNoFunkos(){
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.save(pedido));
        // Verify
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(funkosService, never()).getById(anyLong());
    }
    @Test
    void delete() {
        ObjectId idPedido = new ObjectId();
        Pedido pedidoToDelito = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToDelito));
        pedidosService.delete(idPedido);
        verify(pedidosRepository, times(1)).delete(pedidoToDelito);
        verify(pedidosRepository, never()).save(any(Pedido.class));
    }
    @Test
    void deleteNotFound() {
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.delete(idPedido));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).deleteById(idPedido);
    }
    @Test
    void update(){
        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(15.00)
                .build();
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToupdate = new Pedido();
        pedidoToupdate.setLineasPedido(List.of(lineaPedido));
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToupdate));
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToupdate);
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);
        Pedido result = pedidosService.update(idPedido, pedido);
        assertAll(
                () -> assertEquals(pedidoToupdate, result),
                () -> assertEquals(pedidoToupdate.getLineasPedido(), result.getLineasPedido()),
                () -> assertEquals(pedidoToupdate.getLineasPedido().size(), result.getLineasPedido().size())
        );
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository).save(any(Pedido.class));
        verify(funkosService, times(3)).getById(anyLong());
    }
    @Test
    void updateNotFound(){
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.update(idPedido, pedido));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(funkosService, never()).getById(anyLong());
    }
    @Test
    void reserveStock(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);
        when(mapper.toDto(funkoTest)).thenReturn(funkoDto);
        Pedido result = pedidosService.reserveStock(pedido);
        assertAll(
                () -> assertEquals(pedido, result),
                () -> assertEquals(pedido.getLineasPedido(), result.getLineasPedido()),
                () -> assertEquals(pedido.getLineasPedido().size(), result.getLineasPedido().size())
        );
        verify(funkosService, times(1)).getById(anyLong());
        verify(funkosService, times(1)).update(anyLong(), any(FunkoDto.class));
    }
    @Test
    void reserveStockNoLineas(){
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.reserveStock(pedido));
        verify(funkosService, never()).getById(anyLong());
        verify(funkosService, never()).update(anyLong(), any(FunkoDto.class));
    }
    @Test
    void returnStockPedido(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);
        when(mapper.toDto(funkoTest)).thenReturn(funkoDto);
        when(funkosService.update(funkoTest.getId(), funkoDto)).thenReturn(funkoTest);
        Pedido result = pedidosService.returnStockPedido(pedido);
        assertEquals(12, funkoTest.getStock());
        assertEquals(pedido, result);
        verify(funkosService, times(1)).getById(anyLong());
        verify(funkosService, times(1)).update(funkoTest.getId(), funkoDto);
    }
    @Test
    void checkPedido(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(15.00)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);
        assertDoesNotThrow(() -> pedidosService.checkPedido(pedido));
        verify(funkosService, times(1)).getById(anyLong());
    }
    @Test
    void checkPedidoNoLineas(){
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.checkPedido(pedido));
        verify(funkosService, never()).getById(anyLong());
    }
    @Test
    void checkPedidoStockUnderCantidad(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(11)
                .precioProducto(19.99)
                .build();
        lineaPedido1.setIdProducto(1L);
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);
        assertThrows(ProductoNotStock.class, () -> pedidosService.checkPedido(pedido));
        verify(funkosService, times(1)).getById(anyLong());
    }
    @Test
    void checkPedidoPrecioNotMatchProduct(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(20.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getById(anyLong())).thenReturn(funkoTest);
        assertThrows(ProductoBadPrice.class, () -> pedidosService.checkPedido(pedido));
        verify(funkosService, times(1)).getById(anyLong());
    }

}
