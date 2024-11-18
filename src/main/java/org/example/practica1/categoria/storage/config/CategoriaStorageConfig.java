package org.example.practica1.categoria.storage.config;


import lombok.extern.slf4j.Slf4j;
import org.example.practica1.categoria.storage.service.CategoriaStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CategoriaStorageConfig {
    @Bean
    public CommandLineRunner initCategoryStorageProperties(CategoriaStorageService storageService, @Value("${upload.delete}") String deleteAll) {
        return args -> {

            if (deleteAll.equals("true")) {
                log.info("Borrando ficheros de almacenamiento...");
                storageService.deleteAll();
            }

            storageService.init(); // inicializamos
        };
    }
    private String location = "jsons";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
