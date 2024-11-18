package org.example.practica1.funko.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.exceptions.FunkoNotFound;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.repository.FunkoRepository;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.storage.service.StorageService;
import org.example.practica1.websocket.config.WebSocketConfig;
import org.example.practica1.websocket.config.WebSocketHandler;
import org.example.practica1.websocket.notifications.dto.FunkoNotificationResponse;
import org.example.practica1.websocket.notifications.mapper.FunkoNotificationMapper;
import org.example.practica1.websocket.notifications.models.Notificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = {"funkos"})
@Service
public class FunkosServiceImpl implements FunkosService{

    private final Logger logger = LoggerFactory.getLogger(FunkosServiceImpl.class);

    private final FunkoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final StorageService storageService;
    private final Mapper mapper;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final FunkoNotificationMapper funkoNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public FunkosServiceImpl(FunkoRepository repository, CategoriaRepository categoriaRepository, StorageService storageService, Mapper mapper, WebSocketConfig webSocketConfig, FunkoNotificationMapper funkoNotificationMapper, WebSocketHandler webSocketHandler) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.storageService = storageService;
        this.mapper = mapper;

        this.webSocketConfig = webSocketConfig;
        objectMapper = new ObjectMapper();
        this.funkoNotificationMapper = funkoNotificationMapper;
        this.webSocketService = webSocketConfig.webSocketFunkosHandler();
    }

    @Override
    public Page<Funko> getAll(Optional<String> nombre, Optional<Integer> precio, Optional<String> categoria, Pageable pageable) {

        Specification<Funko> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specPrecio = (root, query, criteriaBuilder) ->
                precio.map(p -> criteriaBuilder.equal(root.get("precio"), p))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specCategoria = (root, query, criteriaBuilder) ->
                categoria.map(c -> criteriaBuilder.like(criteriaBuilder.lower(root.join("categoria").get("nombre")), "%" + c.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));



        Specification<Funko> criterio = Specification.where(specNombre)
                .and(specPrecio)
                .and(specCategoria);

        return repository.findAll(criterio, pageable);
    }


    @Override
    //@Cacheable(key = "#id")
    public Funko getById(Long id) {
        return repository.findById(id).orElseThrow(()-> new FunkoNotFound(id));
    }

    @Override
    //@Cacheable(key = "#funko.id")
    public Funko create(FunkoDto funkoDto) {

        Categoria categoria = categoriaRepository.findById(funkoDto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFound(funkoDto.categoriaId()));
        var funko = mapper.fromDto(funkoDto,categoria);

        var funkoSaved=repository.save(funko);

        onChange(Notificacion.Tipo.CREATE,funkoSaved);

        return funkoSaved;
    }

    @Override
    //@Cacheable(key = "#result.id")
    public Funko update(Long id, FunkoDto funkoDto) {

        Categoria categoria = categoriaRepository.findById(funkoDto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFound(funkoDto.categoriaId()));

        var funko = mapper.fromDto(funkoDto,categoria);

        var res = repository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        res.setNombre(funko.getNombre());
        res.setPrecio(funko.getPrecio());
        res.setUpdatedAt(LocalDateTime.now());
        res.setCategoria(funko.getCategoria());
        res.setDescripcion(funko.getDescripcion());
        res.setImagen(funko.getImagen());

        var funkoUpdated= repository.save(res);

        onChange(Notificacion.Tipo.UPDATE, funkoUpdated);

        return funkoUpdated;
    }

    void onChange(Notificacion.Tipo tipo, Funko data) {

        if (webSocketService == null) {
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notificacion<FunkoNotificationResponse> notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toProductNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
        }
    }

    @Override
    public Funko updateImage(Long id, MultipartFile image) {
        var funko = this.getById(id);
        String imageStored = storageService.store(image);
        String imageUrl = imageStored;
        var funkoActualizado = new Funko(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCategoria(),
                funko.getDescripcion(),
                imageUrl,
                funko.getStock(),
                funko.getCreatedAt(),
                LocalDateTime.now()
        );

        var funkoUpdated= repository.save(funkoActualizado);

        onChange(Notificacion.Tipo.UPDATE, funkoUpdated);


        return funkoUpdated;
    }

    @Override
    //@CacheEvict(key = "#id")
    public Funko delete(Long id) {
        Funko funko = repository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        repository.deleteById(id);
        if (funko.getImagen() != null && !funko.getImagen().equals(Funko.IMAGE_DEFAULT)) {
            storageService.delete(funko.getImagen());
        }

        onChange(Notificacion.Tipo.DELETE, funko);

        return funko;
    }

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}
