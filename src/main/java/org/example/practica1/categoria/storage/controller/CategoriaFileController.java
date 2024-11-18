package org.example.practica1.categoria.storage.controller;


import lombok.extern.slf4j.Slf4j;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.storage.service.CategoriaStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.path:/api}/${api.version:/v1}/categorias/ficheros")
@Slf4j
public class CategoriaFileController {


    private final CategoriaStorageService storageService;

    @Autowired
    public CategoriaFileController(CategoriaStorageService storageService) {
        this.storageService = storageService;
    }

    @PutMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<Categoria> uploadFile(@RequestPart("file") MultipartFile file) {

        log.info("Uploading file");
        storageService.store(file);
        if (!file.isEmpty()) {
            var categorias = storageService.readJson(file);
            return categorias;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado un json para las categorias o esta está vacía");
        }
    }

    @GetMapping("{name}")
    @ResponseBody
    public ResponseEntity<Resource> serveDefaultFile(@PathVariable String name) {

        storageService.createDefaultJson(name+".json");
        Resource file = storageService.loadAsResource(name+".json");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+name+"\"") // Forzar descarga con nombre 'categories.json'
                .body(file);
    }

}
