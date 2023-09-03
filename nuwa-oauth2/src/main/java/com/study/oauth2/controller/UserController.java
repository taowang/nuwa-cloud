package com.study.oauth2.controller;

import com.study.platform.base.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserController
 * @DESCRIPTION:
 * @author: 西门
 * @create: 2023-04-09 15:10
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    //生成密码
    @GetMapping("/genPassWord")
    public Result<String> genPassWord() {
        return Result.success("genPassWord");
    }
}
