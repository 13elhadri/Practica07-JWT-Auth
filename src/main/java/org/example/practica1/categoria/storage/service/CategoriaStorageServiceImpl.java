package org.example.practica1.categoria.storage.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.example.practica1.categoria.storage.controller.CategoriaFileController;
import org.example.practica1.storage.exceptions.StorageBadRequest;
import org.example.practica1.storage.exceptions.StorageInternal;
import org.example.practica1.storage.exceptions.StorageNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class CategoriaStorageServiceImpl implements CategoriaStorageService{

    private final Path rootLocationRead;
    private final Path rootLocationWrite;
    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    @Autowired
    public CategoriaStorageServiceImpl(@Value("${upload-jsons.root-location.read}") String pathr,@Value("${upload-jsons.root-location.write}") String pathw, CategoriaService categoriaService, CategoriaMapper categoriaMapper) {
        this.rootLocationRead = Paths.get(pathr);
        this.rootLocationWrite = Paths.get(pathw);
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper;
    }


    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocationRead);
            Files.createDirectories(rootLocationWrite);
        } catch (IOException e) {
            throw new StorageBadRequest("No se puede inicializar el almacenamiento " + e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);
        String justFilename = filename.replace("." + extension, "");
        String storedFilename = System.currentTimeMillis() + "_" + justFilename + "." + extension;

        try {
            if (file.isEmpty()) {
                throw new StorageNotFound("Fichero vacío " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageInternal(
                        "No se puede almacenar un fichero con una ruta relativa fuera del directorio actual "
                                + filename);
            }
            if (!extension.equals("json")) {
                throw new StorageInternal("El fichero debe ser un JSON " + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                log.info("Almacenando fichero " + filename + " como " + storedFilename);
                Files.copy(inputStream, this.rootLocationRead.resolve(storedFilename),
                        StandardCopyOption.REPLACE_EXISTING);
                return storedFilename;
            }

        } catch (IOException e) {
            throw new StorageBadRequest("Fallo al almacenar fichero " + filename + " " + e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocationRead, 1)
                    .filter(path -> !path.equals(this.rootLocationRead))
                    .map(this.rootLocationRead::relativize);
        } catch (IOException e) {
            throw new StorageBadRequest("Fallo al leer ficheros almacenados " + e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocationWrite.resolve(filename);    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageBadRequest("No se puede leer ficheroooo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageBadRequest("Error al cargar fichero " + filename + " " + e);
        }
    }

    @Override
    public void delete(String filename) {
        String justFilename = StringUtils.getFilename(filename);

        try {
            log.info("Eliminando fichero " + filename);
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageBadRequest("No se puede eliminar el fichero " + filename + " " + e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocationRead.toFile());

    }

    @Override
    public String getUrl(String filename) {
        return MvcUriComponentsBuilder
                .fromMethodName(CategoriaFileController.class, "serveFile", filename, null)
                .build().toUriString();
    }


    @Override
    public List<Categoria> readJson(MultipartFile file) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(file.getInputStream());
            List<Categoria> categorias = new ArrayList<>();

            if (root.isArray()) {
                categorias = mapper.convertValue(root, new TypeReference<List<Categoria>>() {});
            } else if (root.isObject()) {
                Categoria categoria = mapper.treeToValue(root, Categoria.class);
                categorias.add(categoria);
            }

            for (Categoria categoria : categorias) {
                if (!StringUtils.isEmpty(categoria.getNombre())) {
                    categoriaService.create(categoria);
                }
            }

            return categorias;
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageBadRequest("Error al leer el JSON " + e);
        }
    }

    @Override
    public void createDefaultJson(String name) {
        ObjectMapper mapper = new ObjectMapper();

        Path filePath = rootLocationWrite.resolve(name);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());


        var categorias = categoriaService.getAll(Optional.empty(),Optional.empty(),pageable);

        var categoriasDto = categorias.stream().map(categoriaMapper::toDto).toList();


        try {
            mapper.writeValue(filePath.toFile(), categoriasDto);
            log.info("Archivo default.json creado en " + filePath);
        } catch (IOException e) {
            log.error("Error al crear default.json: " + e.getMessage());
            throw new StorageBadRequest("No se pudo crear el archivo default.json");
        }
    }

}
