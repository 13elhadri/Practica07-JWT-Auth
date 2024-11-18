package org.example.practica1.funko.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.descripcion.models.Descripcion;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;




@Data
@Entity
@Table(name = "funkos")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funko{
    public static final String IMAGE_DEFAULT = "/default/default.jpg";


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String nombre;

    @Column(nullable = false)
    @Min(value = 1, message = "El precio debe ser mayor que 10")
    @Max(value = 100, message = "El precio debe ser menor que 100")
    private int precio;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Embedded
    private Descripcion descripcion;

    @Builder.Default
    @Column(columnDefinition = "TEXT default '" + IMAGE_DEFAULT + "'")
    private String imagen = IMAGE_DEFAULT;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(columnDefinition = "integer default 0")
    @Builder.Default
    private Integer stock = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
