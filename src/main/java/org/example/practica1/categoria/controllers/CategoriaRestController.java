package org.example.practica1.categoria.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.example.practica1.pagination.PageResponse;
import org.example.practica1.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RestController
@RequestMapping("${api.path:/api}/${api.version:/v1}/categorias")
public class CategoriaRestController {

    private final CategoriaService service;
    private final CategoriaMapper mapper;
    private final PaginationLinksUtils paginationLinksUtils;


    @Autowired
    public CategoriaRestController(CategoriaService service, CategoriaMapper mapper, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.mapper = mapper;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Categoria>> getCategorias(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request)
    {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Categoria> pageResult = service.getAll(nombre, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link",paginationLinksUtils.createLinkHeader(pageResult,uriBuilder))
                .body(PageResponse.of(pageResult,sortBy,direction));

    }

    @GetMapping("{id}")
    public ResponseEntity<Categoria> getCategoria(@PathVariable UUID id){return ResponseEntity.ok(service.getById(id));}

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@Valid @RequestBody CategoriaDto categoria){
        var result = service.create(mapper.fromDto(categoria));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable UUID id, @RequestBody CategoriaDto categoria){

        var result= service.update(id,mapper.fromDto(categoria));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
