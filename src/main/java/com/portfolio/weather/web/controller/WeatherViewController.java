package com.portfolio.weather.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherViewController {
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
