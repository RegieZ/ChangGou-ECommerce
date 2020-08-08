package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.service.BrandService;
import com.changgou.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/findAll")
    public Result findAll() {
        List<Brand> brandList = brandService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", brandList);
    }

    /***
     * 根据ID查询品牌数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Brand brand = brandService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",brand);
    }

    /***
     * 新增品牌数据
     * @param brand
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true,StatusCode.OK,"添加成功");
    }
}