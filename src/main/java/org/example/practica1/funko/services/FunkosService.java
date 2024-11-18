package org.example.practica1.funko.services;

import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FunkosService {

    Page<Funko> getAll(Optional<String> nombre, Optional<Integer> precio, Optional<String> categoria, Pageable pageable);

    Funko getById(Long id);

    Funko create(FunkoDto funkoDto);

    Funko update(Long id, FunkoDto funkoDto);

    Funko updateImage(Long id, MultipartFile image);

    Funko delete(Long id);

}
