package org.example.practica1.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.practica1.users.models.Role;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);
}
