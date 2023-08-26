package com.study.oauth2.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author
 * @Description
 * @Date
 */
@RestController
@RequestMapping("/view")
public class ViewController {

    @RequestMapping("/login")
    public String loginView(Model model) {
        return "oauth-error";
    }
}