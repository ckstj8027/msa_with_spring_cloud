package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("order-service")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;


    @GetMapping("/health_check")
    private String status(){
        return String.format("It's working in order service post %s",env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId ,@RequestBody RequestOrder requestOrder) {

        log.info("before add order data");

        ModelMapper mapper = new ModelMapper();

        OrderDto orderDto =mapper.map(requestOrder, OrderDto.class);
        orderDto.setUserId(userId);

//        OrderDto createdOrder = orderService.createOrder(orderDto);
//        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(requestOrder.getQty()*requestOrder.getUnitPrice());

        // 카프카에 오더 샌드
         kafkaProducer.send("example-catalog-topic", orderDto);
        orderProducer.send("orders", orderDto);

        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        log.info("after added order data");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);


    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable String userId) throws Exception {

        log.info("before retrieve order data");
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);

        ModelMapper mapper = new ModelMapper();
        List<ResponseOrder> responseOrders = new ArrayList<>();

        for (OrderEntity order : orders) {
            responseOrders.add(mapper.map(order, ResponseOrder.class));
        }

//        try {
//            Thread.sleep(1000);
//            throw new Exception("장애발생");
//        }catch (InterruptedException ex) {
//            log.error(ex.getMessage());
//        }


        log.info("add retrieve order data");

        return ResponseEntity.status(HttpStatus.OK).body(responseOrders);


    }


//    @GetMapping("/{orderId}")
//    public ResponseEntity<ResponseOrder> getOrder(String orderId) {
//        OrderDto orderDto = orderService.getOrderByOrderId(orderId);
//        ModelMapper modelMapper = new ModelMapper();
//        ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);
//
//        return ResponseEntity.status(HttpStatus.OK).body(responseOrder);
//    }




}
