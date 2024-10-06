package com.study.nuwa.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.nuwa.cloud.entity.SystemTable;

import java.util.List;

public interface ISystemService {

    List<SystemTable> list();

    Page<SystemTable> page();

    String exception();
}
