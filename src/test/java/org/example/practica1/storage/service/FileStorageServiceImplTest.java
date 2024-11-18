package org.example.practica1.storage.service;


import org.example.practica1.storage.exceptions.StorageBadRequest;
import org.example.practica1.storage.exceptions.StorageNotFound;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.class)
@ExtendWith(MockitoExtension.class)
public class FileStorageServiceImplTest {

    @Mock
    private MultipartFile multipartFile;

    private static Path mockRootLocation;

    private FileStorageServiceImpl storageServiceImpl;



    @BeforeEach
    void setUp() throws IOException {
        mockRootLocation = Paths.get("test_imgs");
        if (!Files.exists(mockRootLocation)) {
            Files.createDirectory(mockRootLocation);
        }
        storageServiceImpl = new FileStorageServiceImpl(mockRootLocation.toString());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Files.walk(mockRootLocation)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        Files.deleteIfExists(mockRootLocation);
    }

    @Test
    void init() {
        storageServiceImpl.init();
        assertTrue(Files.exists(mockRootLocation));
    }

    @Test
    void store() throws IOException {
        String filename = "test-image3.png";
        Files.createFile(mockRootLocation.resolve("test-image3.png"));
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));

        String storedFilename = storageServiceImpl.store(multipartFile);
        assertTrue(storedFilename.contains("test-image"));
        verify(multipartFile, times(1)).getInputStream();
    }

    @Test
    void storeEmptyFile() {
        String filename = "test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(true);
        assertThrows(StorageBadRequest.class, () -> storageServiceImpl.store(multipartFile));
    }

    @Test
    void storeFileWithRelativePath() {
        String filename = "../test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(false);
        assertThrows(StorageBadRequest.class, () -> storageServiceImpl.store(multipartFile));
    }

    @Test
    void loadAll() throws IOException {
        Files.createDirectories(mockRootLocation);

        Stream<Path> files = storageServiceImpl.loadAll();
        assertEquals(2, files.count());
    }

    @Test
    void load() {
        Path path = storageServiceImpl.load("test-image.png");
        assertEquals(mockRootLocation.resolve("test-image.png"), path);
    }

    @Test
    void loadAsResource() throws IOException {
        // Crear el archivo "test-image.png" en la ruta mockRootLocation
        Path filePath = mockRootLocation.resolve("test-image.png");
        Files.createFile(filePath);

        Resource resource = storageServiceImpl.loadAsResource("test-image.png");
        assertTrue(resource.exists());
    }

    @Test
    void loadAsResourceNotFound() {
        assertThrows(StorageNotFound.class, () -> storageServiceImpl.loadAsResource("image.png"));
    }

    @Test
    void delete() throws IOException {
        Files.createDirectories(mockRootLocation);
        Files.createFile(mockRootLocation.resolve("test-image9.png"));

        storageServiceImpl.delete("test-image9.png");
        assertFalse(Files.exists(mockRootLocation.resolve("test-image9.png")));
    }

    @Test
    void deleteAll() throws IOException {
        Files.createDirectories(mockRootLocation);
        Files.createFile(mockRootLocation.resolve("test-image10.png"));
        Files.createFile(mockRootLocation.resolve("test-image11.png"));
        assertEquals(2, Files.list(mockRootLocation).count());
        storageServiceImpl.deleteAll();
        assertFalse(Files.exists(mockRootLocation));
    }




    @Test
    void getUrl() {

        String filename = "test-image.png";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/files/" + filename);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
        String url = storageServiceImpl.getUrl(filename);

        assertTrue(url.contains(filename), "La URL no contiene el filename '" + filename + "'");

        RequestContextHolder.resetRequestAttributes();
    }
}
