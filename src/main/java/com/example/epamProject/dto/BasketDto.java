package com.example.epamProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketDto {
    private String username;
    private List<ItemDto> items;


 /*   public static BasketDto getUserBasket(UserEntity user, List<BasketItemEntity> basketItems) {
        BasketDto basketDto = new BasketDto();
        basketDto.setId((long) user.getId());
        basketDto.setUsername(user.getEmail());

        List<BasketItemDto> basketItemDtos = BasketItemDto.fromEntityList(basketItems);
        basketDto.setItems(basketItemDtos);

        return basketDto;
    }*/

}
