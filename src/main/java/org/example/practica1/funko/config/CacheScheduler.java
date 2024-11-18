package org.example.practica1.funko.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheScheduler {
    private final Logger logger = LoggerFactory.getLogger(CacheScheduler.class);

    @Scheduled(fixedRateString = "${caching.spring.personasTTL}")
    @CacheEvict(value = "funkos", allEntries = true)
    public void clearCache() {
        logger.info("Limpiando la caché de funkos");
    }
}
