package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.entity.User;

public interface UserService extends IService<User> {
    String login(String code);
    User getCurrentUser();
} 