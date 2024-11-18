package org.example.practica1.categoria.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.practica1.funko.models.Funko;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "categoria", uniqueConstraints = {@UniqueConstraint(columnNames = "nombre")})
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)", unique = true, nullable = false)
    private Nombre nombre;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;




    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }


    public enum Nombre {
        SERIE, DISNEY, SUPERHEROES, PELICULA, OTROS
    }
}
