package com.example.demo.controller;

import com.example.demo.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class CheckController {

    private CheckService checkService;

    @Autowired
    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    @GetMapping("/check")
    public void check(HttpServletResponse response, @RequestParam int roomId, @RequestParam boolean entrance, @RequestParam int keyId) {
        this.checkService.check(response, roomId, entrance, keyId);
    }
}
