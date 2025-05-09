package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.model.vo.WardrobeVO;
import com.wardrobe.service.WardrobeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：luke
 * @date ：Created in 2025/5/6 15:19
 * @description：
 * @modified By：
 */

@RestController
@RequestMapping("/wardrobe")
public class WardrobeController {

    @Autowired
    private WardrobeService wardrobeService;

    @PostMapping
    public Result<List<WardrobeVO>> allWardrobe() {
        return Result.success(wardrobeService.allWardrobe());
    }
}
