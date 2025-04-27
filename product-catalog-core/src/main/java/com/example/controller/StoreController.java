package com.example.controller;

import com.example.dto.StoreDTO;
import com.example.dto.StoreCreateDTO;
import com.example.entity.Store;
import com.example.mapper.StoreMapper;
import com.example.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreCreateDTO createDTO) {
        Store store = new Store(createDTO.getStoreName(), createDTO.getAddress());
        Store createdStore = storeService.createStore(store);

        StoreDTO storeDTO = StoreMapper.INSTANCE.toDto(createdStore);
        return ResponseEntity.status(HttpStatus.CREATED).body(storeDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        Store store = storeService.getStoreById(id);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }

        StoreDTO storeDTO = StoreMapper.INSTANCE.toDto(store);
        return ResponseEntity.ok(storeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable Long id, @RequestBody Store store) {
        store.setStoreId(id);
        storeService.updateStore(store);

        StoreDTO storeDTO = StoreMapper.INSTANCE.toDto(store);
        return ResponseEntity.ok(storeDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StoreDTO> partialUpdateStore(@PathVariable Long id,
                                                             @RequestBody StoreCreateDTO updateDTO) {
        Store store = storeService.getStoreById(id);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateDTO.getStoreName() != null) {
            store.setStoreName(updateDTO.getStoreName());
        }
        if (updateDTO.getAddress() != null) {
            store.setAddress(updateDTO.getAddress());
        }

        storeService.updateStore(store);
        StoreDTO storeDTO = StoreMapper.INSTANCE.toDto(store);
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

        List<StoreDTO> storeDTOS = StoreMapper.INSTANCE.toDtoList(stores);
        return ResponseEntity.ok(storeDTOS);
    }
}