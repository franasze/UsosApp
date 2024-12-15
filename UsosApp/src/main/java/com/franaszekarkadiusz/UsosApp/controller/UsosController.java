package com.franaszekarkadiusz.UsosApp.controller;

import com.franaszekarkadiusz.UsosApp.service.UsosService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UsosController {


    public UsosController(UsosService usosService) {
        this.usosService = usosService;
    }
    private final UsosService usosService;

    @GetMapping("/usos")
    public String getData(){
        return usosService.getData();
    }

}
