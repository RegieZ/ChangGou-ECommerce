package com.changgou.goods.service;

import com.changgou.pojo.Brand;

import java.util.List;

public interface BrandService {

    /***
     * 查询所有品牌
     * @return
     */
    public List<Brand> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public Brand findById(Integer id);

    /***
     * 新增品牌
     * @param brand
     */
    public void add(Brand brand);
}
