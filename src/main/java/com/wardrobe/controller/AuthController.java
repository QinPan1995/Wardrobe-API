package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.model.dto.LoginRequest;
import com.wardrobe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getCode());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return Result.success(response);
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("测试接口");
    }
} 