package com.sum.order_service.service;


import com.sum.order_service.dto.InventoryResponse;
import com.sum.order_service.dto.OrderLineItemsDto;
import com.sum.order_service.dto.OrderRequest;
import com.sum.order_service.model.Order;
import com.sum.order_service.model.OrderLineItems;
import com.sum.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor //based

@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

//    public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
//        Order order = new Order();
//        order.setOrderNumber(UUID.randomUUID().toString());
//        if (orderLineItems == null || orderLineItems.isEmpty()) {
//            throw new IllegalArgumentException("Order items cannot be empty");
//        }
//
//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList();
//        if (orderLineItems == null || orderLineItems.isEmpty()) {
//            throw new IllegalArgumentException("Order items cannot be empty");
//        }
//        orderLineItems.stream()
//                .map(this::maptoDto)
//                .toList();
//
//        order.setOrderLineItemsList(orderLineItems);
//
//        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
//
//        //Call inventory service and place order if product in place
//       InventoryResponse[] inventoryResponseArray=  webClient.get()
//                .uri("http://localhost:8082/api/inventory")
//                .retrieve()
//                .bodyToMono(InventoryResponse[].class)
//                .block();
//
//        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
//       if(allProductsInStock){
//           orderRepository.save(order);
//       }
//        else{
//            throw new IllegalAccessException("Product not in stock please try again later");
//       }
//
//    }

    public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItemsDto> orderLineItemsDto = orderRequest.getOrderLineItemsDtoList();
        if (orderLineItemsDto == null || orderLineItemsDto.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        List<OrderLineItems> orderLineItems = orderLineItemsDto.stream()
                .map(this::maptoDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Call inventory service with skuCodes
        //      .uri("http://localhost:8082/api/inventory",
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
// Add this debug
        System.out.println("Inventory Response: " + Arrays.toString(inventoryResponseArray));
        System.out.println("SKU Codes: " + skuCodes);
        if (inventoryResponseArray == null || inventoryResponseArray.length == 0) {
            throw new IllegalAccessException("Product not found in inventory");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        if(allProductsInStock){
            orderRepository.save(order);
        } else {
            throw new IllegalAccessException("Product not in stock please try again later");
        }
    }

    private OrderLineItems maptoDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
