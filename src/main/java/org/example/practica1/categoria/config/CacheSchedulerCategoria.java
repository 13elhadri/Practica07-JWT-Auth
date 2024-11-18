package org.example.practica1.categoria.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheSchedulerCategoria {

    @Scheduled(fixedRateString = "${caching.spring.personasTTL}")
    @CacheEvict(value = "categoria", allEntries = true)
    public void clearCache() {
        log.info("Limpiando la caché de categoria");
    }
}
