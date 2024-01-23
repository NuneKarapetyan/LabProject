package com.example.epamProject.service;

import com.example.epamProject.dto.OrderMedicineDto;
import com.example.epamProject.dto.OrdersDto;
import com.example.epamProject.dto.UserDto;
import com.example.epamProject.entity.OrderMedicineEntity;
import com.example.epamProject.entity.OrdersEntity;
import com.example.epamProject.entity.UserEntity;
import com.example.epamProject.repo.MedicineRepository;
import com.example.epamProject.repo.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    /* private final OrderRepository orderRepository;
     private final ModelMapper modelMapper;
     private final UserService userService;

     public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, UserService userService) {
         this.orderRepository = orderRepository;
         this.modelMapper = modelMapper;
         this.userService = userService;
     }

     public List<OrdersDto> getOrders() {

         List<OrdersEntity> ordersEntities = orderRepository.findAll();
         return ordersEntities.stream()
                 .map(this::convertToDTO)
                 .collect(Collectors.toList());
     }

     private OrdersDto convertToDTO(OrdersEntity ordersEntity) {
         return modelMapper.map(ordersEntity, OrdersDto.class);
     }
     public void addNewOrder(OrdersDto order) {
         String email = (String) SecurityContextHolder.getContext().getAuthentication()
                 .getPrincipal();
         UserDto userDto = userService.getByEmail(email);
         OrdersEntity orderEntity = convertToEntity(order, userDto);
         orderRepository.save(orderEntity);
     }

     private OrdersEntity convertToEntity(OrdersDto orderDto, UserDto userDto) {
         OrdersEntity orderEntity = new OrdersEntity();
         orderEntity.setTotalCost(orderDto.getTotalCost());
         orderEntity.setOrderDate(orderDto.getOrderDate());
         orderEntity.setDoctorsNote(orderDto.getDoctorsNote());
         UserEntity userEntity = convertToEntity(userDto);
         orderEntity.setUser(userEntity); // Assuming you have the userEntity available
         return orderEntity;
     }

     private UserEntity convertToEntity(UserDto userDto) {
         UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

         // Assuming you have a method to convert AddressDto to AddressEntity
         AddressEntity addressEntity = convertToEntity(userDto.getAddress());
         userEntity.setAddress(addressEntity);

         // Set other properties as needed

         return userEntity;
     }

     private AddressEntity convertToEntity(AddressDto addressDto) {
         return modelMapper.map(addressDto, AddressEntity.class);
     }
     public void deleteOrder(Long orderId) {
         orderRepository.deleteById(orderId);
     }*/
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MedicineRepository medicineRepository; // Assuming you have a repository for MedicineEntity

    @Autowired
    private ModelMapper modelMapper; // Assuming you have ModelMapper configured

    public void placeOrder(OrdersDto ordersDto) {
        OrdersEntity ordersEntity = convertToEntity(ordersDto);
        orderRepository.save(ordersEntity);
    }
    public ResponseEntity<String> createOrder(OrdersDto orderDto) {
        try {
            placeOrder(orderDto);
            return ResponseEntity.ok("Order placed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to place order: " + e.getMessage());
        }
    }

    private OrdersEntity convertToEntity(OrdersDto ordersDto) {
        OrdersEntity ordersEntity = modelMapper.map(ordersDto, OrdersEntity.class);

        // Assuming you have a method to convert UserDto to UserEntity
        UserEntity userEntity = convertToEntity(ordersDto.getUser());
        ordersEntity.setUser(userEntity);

        // Assuming you have a list of OrderMedicineDto in OrdersDto
        List<OrderMedicineEntity> orderMedicineEntities = convertToOrderMedicineEntities(ordersDto.getOrderMedicines());
        ordersEntity.setOrderMedicines(orderMedicineEntities);

        return ordersEntity;
    }

    private UserEntity convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }

    private List<OrderMedicineEntity> convertToOrderMedicineEntities(List<OrderMedicineDto> orderMedicineDtos) {
        return orderMedicineDtos.stream()
                .map(orderMedicineDto -> modelMapper.map(orderMedicineDto, OrderMedicineEntity.class))
                .collect(Collectors.toList());
    }
}
