package com.changgou.goods.service;

import com.changgou.pojo.Brand;

import java.util.List;

public interface BrandService {

    /***
     * 查询所有品牌
     * @return
     */
    public List<Brand> findAll();
}
