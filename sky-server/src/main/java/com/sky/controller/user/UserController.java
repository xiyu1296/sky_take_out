package com.sky.controller.user;


import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/user")
@Slf4j
@Api(tags = "用户界面相关")
@RestController
public class UserController {

    @Autowired
    private UserService userService;



    @PostMapping("/login")
    @ApiOperation("登录接口")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登陆中：{}",userLoginDTO);

        return Result.success(userService.login(userLoginDTO));

    }
}
