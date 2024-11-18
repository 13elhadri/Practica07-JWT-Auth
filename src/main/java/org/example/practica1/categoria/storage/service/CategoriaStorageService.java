package org.example.practica1.categoria.storage.service;

import org.example.practica1.categoria.models.Categoria;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface CategoriaStorageService {
    void init();


    String store(MultipartFile file);


    Stream<Path> loadAll();


    Path load(String filename);


    Resource loadAsResource(String filename);


    void delete(String filename);


    void deleteAll();


    String getUrl(String filename);

    List<Categoria> readJson(MultipartFile file);

    void  createDefaultJson(String name);
}
