package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("manage")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    /**
     * 导入历史数据
     */
    @GetMapping("importAll")
    public Result importAll() {
        managerService.importAll();
        return new Result("导入所有数据成功", null);
    }
}