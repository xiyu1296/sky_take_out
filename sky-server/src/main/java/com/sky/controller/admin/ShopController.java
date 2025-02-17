package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("admin")
@Api(tags = "店铺控制类")
@Slf4j
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "SHOP_STATUS";

    @PutMapping("/{status}")
    @ApiOperation("修改店铺营业状态")
    public Result changeShopStatus(@PathVariable Integer status){
        log.info("修改店铺营业状态状态为:{}",status);

        redisTemplate.opsForValue().set(KEY,status);

        return Result.success();

    }

    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getShopStatus(){

        log.info("checking shop status");

        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);

        return Result.success(status);

    }

}
