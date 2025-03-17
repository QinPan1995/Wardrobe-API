package com.wardrobe.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.JwtUtil;
import com.wardrobe.mapper.UserMapper;
import com.wardrobe.model.entity.User;
import com.wardrobe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${wx.miniapp.appid}")
    private String appid;

    @Value("${wx.miniapp.secret}")
    private String secret;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(String code) {
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        String openid = json.getStr("openid");
        if (openid == null) {
            throw new RuntimeException("获取openid失败");
        }

        // 查找或创建用户
        User user = lambdaQuery().eq(User::getOpenid, openid).one();
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            save(user);
        }

        return jwtUtil.generateToken(openid);
    }

    @Override
    public User getCurrentUser() {
        String openid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return lambdaQuery().eq(User::getOpenid, openid).one();
    }
} 