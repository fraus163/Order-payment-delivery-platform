package com.fraus.orderservice.api;

import com.fraus.commonlibs.http.order.CreateOrderRequestDto;
import com.fraus.commonlibs.http.order.OrderDto;
import com.fraus.orderservice.domain.db.OrderEntityMapper;
import com.fraus.orderservice.domain.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public OrderDto create(
            @RequestBody CreateOrderRequestDto request
    ) {
        log.info("Creating order: request={}", request);
        var saved = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable(name = "id") Long id) {
        log.info("Retrieving order with id {}", id);
        var found = orderProcessor.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDto(found);
    }

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(
            @PathVariable(name = "id") Long id,
            @RequestBody OrderPaymentRequest request
    ) {
        log.info("Paying order with id {}, request={}", id, request);
        var entity = orderProcessor.processPayment(id, request);
        return orderEntityMapper.toOrderDto(entity);
    }
}
