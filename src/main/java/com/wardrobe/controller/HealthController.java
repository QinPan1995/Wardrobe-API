package com.wardrobe.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthController {


    @RequestMapping("/health")
    public String sayHi() {
        return "hello world";
    }


}
