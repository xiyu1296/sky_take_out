package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceimpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Transactional
    public void insert(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //插入dish
        dishMapper.insert(dish);
        long dishId = dish.getId();

        //插入dishflavor
        List<DishFlavor> dfl = dishDTO.getFlavors();
        if(dfl != null && !dfl.isEmpty()){
            dfl.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });

            dishFlavorMapper.insert(dfl);

        }

    }

    public PageResult page(DishPageQueryDTO dishPageQueryDTO){

        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> voPage = dishMapper.page(dishPageQueryDTO);

        return new PageResult(
                voPage.getTotal(),
                voPage.getResult()
        );

    }

    @Transactional
    public void delete(List<Long> ids){
        //启用不能删
        for(Long id: ids){
            if(Objects.equals(dishMapper.selectStatusById(id), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //套餐不能删
        List<Long> setMealIds = setmealMapper.getSetmealIdsByDishIds(ids);
        if(setMealIds != null && !setMealIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

            dishMapper.deleteByIds(ids);
            dishFlavorMapper.deleteByDishIds(ids);


    }

    public DishVO getDishWithFlavorByid(Long id){
        Dish dish = dishMapper.selectById(id);

        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);

        DishVO dishVO = new DishVO() ;
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

}
