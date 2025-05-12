package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.DropdownService;

import java.util.Map;

@RestController
@RequestMapping("/api/dropdowns")
public class DropdownController {

    @Autowired
    private DropdownService dropdownService;

    @GetMapping("/{language}")
    public ResponseEntity<Map<String, Object>> getDropdowns(@PathVariable String language) {
        Map<String, Object> dropdowns = dropdownService.getDropdownData(language);
        return ResponseEntity.ok(dropdowns);
    }

    @GetMapping("/categories")
    public ResponseEntity<String[]> getCategories() {
        String[] categories = dropdownService.getDropdownCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/{language}")
    public ResponseEntity<?> updateDropdowns(@PathVariable String language, @RequestBody Map<String, Object> dropdownData) {
        try {
            dropdownService.saveDropdownData(language, dropdownData);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Dropdown data updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PutMapping("/{language}/{category}")
    public ResponseEntity<?> updateCategoryValues(
            @PathVariable String language,
            @PathVariable String category,
            @RequestBody Object values) {
        try {
            dropdownService.updateCategoryValues(language, category, values);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Category values updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{language}/{category}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable String language,
            @PathVariable String category) {
        try {
            dropdownService.deleteCategory(language, category);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Category deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/{language}/{category}")
    public ResponseEntity<?> addCategory(
            @PathVariable String language,
            @PathVariable String category,
            @RequestBody Object values) {
        try {
            dropdownService.addCategory(language, category, values);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("status", "success", "message", "Category added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}