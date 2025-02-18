package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserServiceimp implements UserService {


    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;

    public UserLoginVO login(UserLoginDTO userLoginDTO){

        //用微信接口服务，获得当前微信用户的openid
        String open_id = getOpenId(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(open_id == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //从数据库获取用户信息
        User user = userMapper.selectByOpenid(open_id);

        //如果是新用户w，自动完成注册
        if(user ==null){
            user = User.builder()
                        .openid(open_id)
                                .createTime(LocalDateTime.now())
                                        .build();
            userMapper.insert(user);
        }

        //生成jwt令牌
        Map<String,Object> claim = new HashMap<>();
        claim.put("userId",user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claim);

        //返回这个用户对象
        return new UserLoginVO(user.getId(),open_id,token);

    }

    private String getOpenId(String code) {

        Map<String ,String> js = new HashMap<>();
        js.put("appid",weChatProperties.getAppid());
        js.put("secret",weChatProperties.getSecret());
        js.put("js_code",code);
        js.put("grant_type","authorization_code");


        String json = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", js);
        //把获取json的openid为字符串
        JSONObject jsonObject = JSONObject.parseObject(json);


        return jsonObject.getString("openid");

    }


}
