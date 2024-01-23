package com.example.epamProject.controller;

import com.example.epamProject.dto.OrdersDto;
import com.example.epamProject.dto.UserDto;
import com.example.epamProject.service.OrderService;
import com.example.epamProject.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;



    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

   /* @GetMapping
    public List<OrdersDto> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    public void addNewOrder(@RequestBody OrdersDto order) {
        orderService.addNewOrder(order);
    }

    @DeleteMapping(path = "{orderId}")
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }*/
   @PostMapping("/place")
   public ResponseEntity<String> placeOrder(@RequestBody OrdersDto ordersDto) {
       try {
           orderService.placeOrder(ordersDto);
           return ResponseEntity.ok("Order placed successfully!");
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place order: " + e.getMessage());
       }
   }
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                              @RequestBody OrderCreateRequest orderRequest) {

        // Extract the token from the "Authorization" header
        String jwtToken = token.replace("Bearer ", "");

        // Retrieve user information from the token
        String email = (String) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        UserDto userDto = userService.getByEmail(email);

        // Build the order DTO with user information
        OrdersDto orderDto = new OrdersDto();
        orderDto.setTotalCost(orderRequest.getTotalCost());
        orderDto.setOrderDate(orderRequest.getOrderDate());
        orderDto.setDoctorsNote(orderRequest.getDoctorsNote());
        orderDto.setUser(userDto);
        orderDto.setOrderMedicines(orderRequest.getOrderMedicines());

        // Call the service to create the order
        ResponseEntity<String> response = orderService.createOrder(orderDto);

        return response;
    }
}

