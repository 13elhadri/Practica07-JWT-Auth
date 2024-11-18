package org.example.practica1.descripcion.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Embeddable
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Descripcion {

    private String descripcion;

    @LastModifiedDate
    private LocalDateTime updateAt;

    public Descripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
