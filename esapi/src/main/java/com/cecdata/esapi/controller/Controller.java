package com.cecdata.esapi.controller;

import com.cecdata.esapi.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private EsService esService;

    @PostMapping("/api")
    public String trigger(@RequestBody String content){
        return esService.triggerPoint(content);
    }
}
