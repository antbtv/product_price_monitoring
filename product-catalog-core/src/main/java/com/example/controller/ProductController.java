package com.example.controller;

import com.example.dto.ProductDTO;
import com.example.dto.ProductCreateDTO;
import com.example.dto.StoreDTO;
import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.CategoryService;
import com.example.service.DataLogService;
import com.example.service.ProductService;
import com.example.service.security.UserService;
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
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final DataLogService dataLogService;
    private final UserService userService;

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    public ProductController(ProductService productService, CategoryService categoryService,
                             ProductMapper productMapper, DataLogService dataLogService,
                             UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
        this.dataLogService = dataLogService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductCreateDTO createDTO) {
        Product product = new Product(createDTO.getProductName(),
                categoryService.getCategoryById(createDTO.getCategoryId()));
        Product createdProduct = productService.createProduct(product);

        ProductDTO productDTO = productMapper.toDto(createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO productDTO = productMapper.toDto(product);
        return ResponseEntity.ok(productDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setProductId(id);
        productService.updateProduct(product);

        ProductDTO productDTO = productMapper.toDto(product);
        return ResponseEntity.ok(productDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> partialUpdateProduct(@PathVariable Long id, @RequestBody ProductCreateDTO updateDTO) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateDTO.getProductName() != null) {
            product.setProductName(updateDTO.getProductName());
        }
        if (updateDTO.getCategoryId() != null) {
            product.setCategory(categoryService.getCategoryById(updateDTO.getCategoryId()));
        }

        productService.updateProduct(product);
        ProductDTO productDTO = productMapper.toDto(product);
        return ResponseEntity.ok(productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();

        List<ProductDTO> productDTOS = productMapper.toDtoList(products);
        return ResponseEntity.ok(productDTOS);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);

        List<ProductDTO> productDTOS = productMapper.toDtoList(products);
        return ResponseEntity.ok(productDTOS);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportProducts() throws IOException {
        byte[] data = productService.exportProductsToJson();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

        dataLogService.logOperation("EXPORT", "products",
                (long) productService.getAllProducts().size(), userService.getCurrentUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDTO>> importProducts(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductDTO> productDTOS = productService.importProductsFromJson(file.getBytes());

            dataLogService.logOperation("IMPORT", "products",
                    (long) productService.getAllProducts().size(), userService.getCurrentUser());

            return ResponseEntity.ok(productDTOS);
        } catch (IOException e) {
            logger.error("Ошибка в импорте данных " + file.getOriginalFilename() + " "  + file.getSize());
            return ResponseEntity.internalServerError().build();
        }
    }
}