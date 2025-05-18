package com.example.controller;

import com.example.dto.StoreDTO;
import com.example.dto.StoreCreateDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.example.service.DataLogService;
import com.example.service.StoreService;
import com.example.service.security.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;
    private final StoreMapper storeMapper;
    private final DataLogService dataLogService;
    private final UserService userService;
    
    private static final Logger logger = LogManager.getLogger(StoreController.class);

    public StoreController(StoreService storeService, StoreMapper storeMapper,
                           DataLogService dataLogService, UserService userService) {
        this.storeService = storeService;
        this.storeMapper = storeMapper;
        this.dataLogService = dataLogService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreCreateDTO createDTO) {
        Store store = new Store(createDTO.getStoreName(), createDTO.getAddress());
        Store createdStore = storeService.createStore(store);

        StoreDTO storeDTO = storeMapper.toDto(createdStore);
        return ResponseEntity.status(HttpStatus.CREATED).body(storeDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        Store store = storeService.getStoreById(id);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }

        StoreDTO storeDTO = storeMapper.toDto(store);
        return ResponseEntity.ok(storeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable Long id, @RequestBody Store store) {
        store.setStoreId(id);
        storeService.updateStore(store);

        StoreDTO storeDTO = storeMapper.toDto(store);
        return ResponseEntity.ok(storeDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StoreDTO> partialUpdateStore(@PathVariable Long id,
                                                             @RequestBody StoreCreateDTO creteDTO) {
        Store store = storeService.getStoreById(id);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }

        if (creteDTO.getStoreName() != null) {
            store.setStoreName(creteDTO.getStoreName());
        }
        if (creteDTO.getAddress() != null) {
            store.setAddress(creteDTO.getAddress());
        }

        storeService.updateStore(store);
        StoreDTO storeDTO = storeMapper.toDto(store);
        return ResponseEntity.ok(storeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<Store> stores = storeService.getAllStores();

        List<StoreDTO> storeDTOS = storeMapper.toDtoList(stores);
        return ResponseEntity.ok(storeDTOS);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportStores() throws IOException {
        byte[] data = storeService.exportStoresToJson();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

        dataLogService.logOperation("EXPORT", "stores",
                (long) storeService.getAllStores().size(), userService.getCurrentUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"stores.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(resource);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StoreDTO>> importStores(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<StoreDTO> storeDTOS = storeService.importStoresFromJson(file.getBytes());

            dataLogService.logOperation("IMPORT", "stores",
                    (long) storeDTOS.size(), userService.getCurrentUser());

            return ResponseEntity.ok(storeDTOS);
        } catch (IOException e) {
            logger.error("Ошибка в импорте данных " + file.getOriginalFilename() + " "  + file.getSize());
            return ResponseEntity.internalServerError().build();
        }
    }
}