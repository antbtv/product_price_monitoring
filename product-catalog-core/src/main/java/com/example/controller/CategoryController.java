package com.example.controller;

import com.example.dto.CategoryDTO;
import com.example.dto.CategoryCreateDTO;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private static final Logger logger = LogManager.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryCreateDTO createDTO) {
        Category parentCategory = null;

        if (createDTO.getParentId() != null) {
            parentCategory = categoryService.getCategoryById(createDTO.getParentId());
        }
        Category category = new Category(createDTO.getCategoryName(), parentCategory);
        Category createdCategory = categoryService.createCategory(category);

        CategoryDTO categoryDTO = CategoryMapper.INSTANCE.toDto(createdCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryDTO categoryDTO = CategoryMapper.INSTANCE.toDto(category);
        return ResponseEntity.ok(categoryDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setCategoryId(id);
        categoryService.updateCategory(category);

        CategoryDTO categoryDTO = CategoryMapper.INSTANCE.toDto(category);
        return ResponseEntity.ok(categoryDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDTO> partialUpdateCategory(@PathVariable Long id,
                                                             @RequestBody CategoryCreateDTO updateDTO) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateDTO.getCategoryName() != null) {
            category.setCategoryName(updateDTO.getCategoryName());
        }
        if (updateDTO.getParentId() != null) {
            category.setParent(categoryService.getCategoryById(updateDTO.getParentId()));
        }

        categoryService.updateCategory(category);
        CategoryDTO categoryDTO = CategoryMapper.INSTANCE.toDto(category);
        return ResponseEntity.ok(categoryDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        List<CategoryDTO> categoryDTOS = CategoryMapper.INSTANCE.toDtoList(categories);
        return ResponseEntity.ok(categoryDTOS);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportCategories() throws IOException {
        byte[] data = categoryService.exportCategoriesToJson();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"categories.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryDTO>> importCategories(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<CategoryDTO> categoryDTOS = categoryService.importCategoriesFromJson(file.getBytes());
            return ResponseEntity.ok(categoryDTOS);
        } catch (IOException e) {
            logger.error("Ошибка в импорте данных " + file.getOriginalFilename() + " "  + file.getSize());
            return ResponseEntity.internalServerError().build();
        }
    }
}