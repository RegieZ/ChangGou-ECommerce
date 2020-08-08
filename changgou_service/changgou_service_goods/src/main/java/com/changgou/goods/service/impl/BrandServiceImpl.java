package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.service.BrandService;
import com.changgou.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Brand findById(Integer id){
        return  brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加
     * @param brand
     */
    @Override
    public void add(Brand brand){
        brandMapper.insertSelective(brand);
    }

    /**
     * 修改
     * @param brand
     */
    @Override
    public void update(Brand brand){
        brandMapper.updateByPrimaryKeySelective(brand);
    }
}
