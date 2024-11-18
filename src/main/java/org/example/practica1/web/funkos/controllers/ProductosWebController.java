package org.example.practica1.web.funkos.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.services.FunkosService;
import org.example.practica1.web.funkos.store.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/funkos")
@Slf4j
public class ProductosWebController {
    private final FunkosService funkosService;
    private final CategoriaService categoriasService;
    private final MessageSource messageSource;
    private final UserStore userSession;

    @Autowired
    public ProductosWebController(FunkosService funkosService, CategoriaService categoriasService, MessageSource messageSource, UserStore userSession) {
        this.funkosService = funkosService;
        this.categoriasService = categoriasService;
        this.messageSource = messageSource;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        log.info("Login GET");
        if (isLoggedAndSessionIsActive(session)) {
            log.info("Si está logueado volvemos al index");
            return "redirect:/funkos";
        }
        return "funkos/login";
    }

    @PostMapping
    public String login(@RequestParam("password") String password, HttpSession session, Model model) {
        log.info("Login POST");
        if ("pass".equals(password)) {
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(1800);
            return "redirect:/funkos";
        } else {
            return "funkos/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout GET");
        session.invalidate();
        return "redirect:/funkos";
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(HttpSession session, Model model, @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction,
                        Locale locale) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var funkosPage = funkosService.getAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        String welcomeMessage = messageSource.getMessage("welcome.message", null, locale);

        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        sessionData.incrementLoginCount();
        var numVisitas = sessionData.getLoginCount();
        var lastLogin = sessionData.getLastLogin();
        var localizedLastLoginDate = getLocalizedDate(lastLogin, locale);

        model.addAttribute("funkosPage", funkosPage);
        model.addAttribute("search", search.orElse(""));
        model.addAttribute("welcomeMessage", welcomeMessage);
        model.addAttribute("numVisitas", numVisitas);
        model.addAttribute("lastLoginDate", localizedLastLoginDate);
        return "funkos/index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("Details GET");

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        Funko funko = funkosService.getById(id);
        model.addAttribute("funko", funko);
        return "funkos/details";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        log.info("Create GET");

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        var categorias = categoriasService.getAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getNombre);
        var funko = FunkoDto.builder()
                .imagen("https://via.placeholder.com/150")
                .precio(0)
                .stock(0)
                .build();
        model.addAttribute("funko", funko);
        model.addAttribute("categorias", categorias);
        return "funkos/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("funko") FunkoDto funkoDto, BindingResult result, Model model) {
        log.info("Create POST");
        if (result.hasErrors()) {
            var categorias = categoriasService.getAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getNombre);
            model.addAttribute("categorias", categorias);
            return "funkos/create";
        }
        var funko = funkosService.create(funkoDto);
        return "redirect:/funkos";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        var categorias = categoriasService.getAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getNombre);
        Funko funko = funkosService.getById(id);
        FunkoDto funkoUpdateRequest = FunkoDto.builder()
                .nombre(funko.getNombre())
                .precio(funko.getPrecio())
                .categoriaId(funko.getCategoria().getId())
                .descripcion(funko.getDescripcion().getDescripcion())
                .stock(funko.getStock())
                .imagen(funko.getImagen())
                .build();
        model.addAttribute("funko", funkoUpdateRequest);
        model.addAttribute("categorias", categorias);
        return "funkos/update";
    }

    @PostMapping("/update/{id}")
    public String updateFunko(@PathVariable("id") Long id, @ModelAttribute FunkoDto funkoUpdateRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var categorias = categoriasService.getAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getNombre);
            model.addAttribute("categorias", categorias);
            return "funkos/update";
        }
        log.info("Update POST");
        var res = funkosService.update(id, funkoUpdateRequest);
        return "redirect:/funkos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        funkosService.delete(id);
        return "redirect:/funkos";
    }

    @GetMapping("/update-image/{id}")
    public String showUpdateImageForm(@PathVariable("id") Long funkoId, Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        Funko funko = funkosService.getById(funkoId);
        model.addAttribute("funko", funko);
        return "funkos/update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateFunkoImage(@PathVariable("id") Long funkoId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("Update POST con imagen");
        funkosService.updateImage(funkoId, imagen);
        return "redirect:/funkos";
    }

    private String getLocalizedDate(Date date, Locale locale) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(locale);
        return localDateTime.format(formatter);
    }

    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        log.info("Comprobando si está logueado");
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        return sessionData != null && sessionData.isLogged();
    }
}
