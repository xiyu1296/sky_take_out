package com.sky.service;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ShoppingCartService {
    
    public void add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCart();

    void clean();

    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
