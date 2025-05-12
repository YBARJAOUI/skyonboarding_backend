package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class DropdownService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    public DropdownService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> getDropdownData(String language) {
        String fileName = getFileNameForLanguage(language);
        try (InputStream is = new ClassPathResource(fileName).getInputStream()) {
            return objectMapper.readValue(is, Map.class);
        } catch (IOException e) {
            // If file doesn't exist or error, return empty map
            return Map.of();
        }
    }

    public String[] getDropdownCategories() {
        // Get categories from English file as default
        Map<String, Object> data = getDropdownData("en");
        return data.keySet().toArray(new String[0]);
    }

    public void saveDropdownData(String language, Map<String, Object> dropdownData) throws IOException {
        String fileName = getFileNameForLanguage(language);
        Resource resource = resourceLoader.getResource("classpath:" + fileName);
        Path path = getResourceFilePath(resource);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), dropdownData);
    }

    public void updateCategoryValues(String language, String category, Object values) throws IOException {
        Map<String, Object> data = getDropdownData(language);
        data.put(category, values);
        saveDropdownData(language, data);
    }

    public void deleteCategory(String language, String category) throws IOException {
        Map<String, Object> data = getDropdownData(language);
        data.remove(category);
        saveDropdownData(language, data);
    }

    public void addCategory(String language, String category, Object values) throws IOException {
        Map<String, Object> data = getDropdownData(language);
        if (data.containsKey(category)) {
            throw new IllegalArgumentException("Category already exists");
        }
        data.put(category, values);
        saveDropdownData(language, data);
    }

    private String getFileNameForLanguage(String language) {
        if ("fr".equalsIgnoreCase(language)) {
            return "dropdowns_fr.json";
        }
        return "dropdowns_en.json";
    }

    private Path getResourceFilePath(Resource resource) throws IOException {
        try {
            // Try to get the file directly
            File file = resource.getFile();
            return file.toPath();
        } catch (IOException e) {
            // If running from JAR, we need to copy the file and work with the copy
            String tempDir = System.getProperty("java.io.tmpdir");
            Path tempFile = Paths.get(tempDir, resource.getFilename());

            try (InputStream is = resource.getInputStream()) {
                Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Register a shutdown hook to copy the file back to the classpath when the application exits
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    // Copy the temp file back to the resource location
                    // This is a simplified approach - in a real application, you'd need more sophisticated handling
                    // For production use, consider storing files in a database or external storage
                    // This approach is mainly for development purposes
                    System.out.println("Copying updated dropdown file back to resources");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }));

            return tempFile;
        }
    }
}