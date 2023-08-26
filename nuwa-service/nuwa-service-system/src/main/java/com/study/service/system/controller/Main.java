package com.study.service.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.study.platform.base.result.PageResult;
import com.study.platform.base.result.Result;
import com.study.service.system.dto.CreateRoleDTO;
import com.study.service.system.entity.Role;
import com.study.service.system.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @ClassName: RoleController
 * @DESCRIPTION: Role 前端控制器
 * @author: 西门
 * @create: 2023-05-07 22:00
 **/

@RestController
@RequestMapping(value = "role")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "RoleController|角色相关的前端控制器")
@RefreshScope
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line1 = sc.nextLine();
        int num = Integer.parseInt(line1);

        List<String> errorList = new ArrayList<>();
        int time = 0;
        int distance = 0;
        String preLine = "";
        for (int i = 0; i < num; i++) {
            String line2 = sc.nextLine();
            String[] lineStr = line2.split(",");
            List<String> list = Arrays.asList(lineStr);
            String adn = list.get(list.size() - 2);
            String rdn = list.get(list.size() - 1);
            if (!adn.equals(rdn)) {
                errorList.add(line2);
                continue;
            }
            String lineTime = list.get(1);
            String lineDistance = list.get(2);
            if (time == 0  || distance == 0 || preLine.length()==0) {
                time = Integer.valueOf(lineTime);;
                distance = Integer.valueOf(lineDistance);;
                preLine = line2;
            } else {
                int timeValue = Integer.valueOf(time);
                int timeValue2 = Integer.valueOf(lineTime);
                int t1 = timeValue > timeValue2 ? timeValue - timeValue2 : timeValue2 - timeValue;
                int distanceValue = Integer.valueOf(distance);
                int distanceValu2 = Integer.valueOf(lineDistance);
                int d1 = distanceValue > distanceValu2 ? distanceValue - distanceValu2 : distanceValu2 - distanceValue;
                if (t1 < 60 && d1 > 5) {
                    errorList.add(preLine);
                    errorList.add(line2);
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        if (!CollectionUtils.isEmpty(errorList)) {
            for (int j = 0; j < errorList.size(); j++) {
                sb.append(errorList.get(j)).append(";");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        System.out.println(sb.length() == 0 ? "null" : sb.toString());

    }


}
