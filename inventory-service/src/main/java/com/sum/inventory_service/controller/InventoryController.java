package com.sum.inventory_service.controller;

import com.sum.inventory_service.dto.InventoryResponse;
import com.sum.inventory_service.service.InventoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private final InventoryService inventoryService;

    //http://localhost:8082/api/inventory/iphone13,iphone13-red
    //http://localhost:8082/api/inventory?skuCode=iphone13&skuCode=iphone13-red

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }

}
