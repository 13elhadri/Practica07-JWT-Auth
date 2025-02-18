package org.example.practica1.pedidos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@TypeAlias("Pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Nombre de la colección en MongoDB
@Document("pedidos")
// Para que sepa con qué clase recuperarlo al traerlo con MongoDB y aplicar polimorfismo
public class Pedido {
    // Id de mongo
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    @NotNull(message = "El id del usuario no puede ser nulo")
    private Long idUsuario;
    @NotNull(message = "El id del cliente no puede ser nulo")
    private Cliente cliente;
    @NotNull(message = "El pedido debe tener al menos una línea de pedido")
    private List<LineaPedido> lineasPedido;
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    @Builder.Default()
    private Integer totalItems = 0;
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    @Builder.Default()
    private Double total = 0.0;
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default()
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    private LocalDateTime updatedAt = LocalDateTime.now();
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    @Builder.Default()
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }

    // Podemos añadir los set para que calculen los campos calculados, por ejemplo con las líneas de pedido
    public void setLineasPedido(List<LineaPedido> lineasPedido) {
        this.lineasPedido = lineasPedido;
        this.totalItems = lineasPedido != null ? lineasPedido.size() : 0;
        this.total = lineasPedido != null ? lineasPedido.stream().mapToDouble(LineaPedido::getTotal).sum() : 0.0;
    }
}
