package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public  class ShoppingCartServiceimpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void add(ShoppingCartDTO shoppingCartDTO){
        ShoppingCart shop = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shop);
        shop.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> lsshpc = shoppingCartMapper.list(shop);

        if(lsshpc !=null && !lsshpc.isEmpty()){
            ShoppingCart shoppingCart = lsshpc.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        }
        else {

            Long DishId = shop.getDishId();
            if (DishId != null) {

                Dish ds = dishMapper.selectById(DishId);

                shop.setName(ds.getName());
                shop.setAmount(ds.getPrice());
                shop.setImage(ds.getImage());

            } else {

                Long setmealId = shop.getSetmealId();
                Setmeal sl = setmealMapper.getById(setmealId);

                shop.setName(sl.getName());
                shop.setAmount(sl.getPrice());
                shop.setImage(sl.getImage());

            }

            shop.setCreateTime(LocalDateTime.now());
            shop.setNumber(1);
            shoppingCartMapper.insert(shop);

        }

    }

    public List<ShoppingCart> showShoppingCart(){

        Long Id = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(Id);

        return  shoppingCartMapper.list(shoppingCart);

    }

    public void clean() {
        Long Id  = BaseContext.getCurrentId();
        shoppingCartMapper.clean(Id);
    }

    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list != null && list.size() > 0){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }

}
