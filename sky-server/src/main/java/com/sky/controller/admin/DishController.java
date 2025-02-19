package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.impl.DishServiceimpl;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品控制类")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    private void deleteInRedis(String pattern){
        Set keyInRedis = new HashSet<>();
        Set tmp = new HashSet<>();
        tmp = redisTemplate.keys(pattern);
        if(tmp!=null && !tmp.isEmpty()){
            keyInRedis = tmp;
            redisTemplate.delete(keyInRedis);
        }
    }

    @PostMapping
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){

        dishService.insert(dishDTO);

        String pat = "dish_" + dishDTO.getCategoryId();

        deleteInRedis(pat);

        return Result.success();

    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("正在分页查询:{}",dishPageQueryDTO);

        PageResult pageresult= dishService.page(dishPageQueryDTO);

        return Result.success(pageresult);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品的id:{}",ids);

        dishService.delete(ids);

        String pat = "dish_*";
        deleteInRedis(pat);

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id回显菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id:{}查询菜品",id);

        DishVO dishVO = dishService.getDishWithFlavorByid(id);

        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("根据dto修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("根据dto修改菜品:{}",dishDTO);

        dishService.update(dishDTO);

        String pat = "dish_*";
        deleteInRedis(pat);

        return Result.success();
    }


}
