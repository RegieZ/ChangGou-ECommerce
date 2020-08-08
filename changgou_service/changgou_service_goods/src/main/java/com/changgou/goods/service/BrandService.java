package com.changgou.goods.service;

import com.changgou.pojo.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /***
     * 查询所有品牌
     * @return
     */
    public List<Brand> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    public Brand findById(Integer id);

    /***
     * 新增品牌
     * @param brand
     */
    public void add(Brand brand);

    /***
     * 修改品牌数据
     * @param brand
     */
    public void update(Brand brand);

    /***
     * 删除品牌
     * @param id
     */
    public void delete(Integer id);

    /***
     * 多条件搜索品牌方法
     * @param searchMap
     * @return
     */
    public List<Brand> findList(Map<String, Object> searchMap);
}
