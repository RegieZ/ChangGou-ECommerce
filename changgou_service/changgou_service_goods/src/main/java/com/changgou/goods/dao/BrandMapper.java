package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据分类名称查询品牌列表
     * @Param("") 用于sql中的参数名称与方法参数名称不一致时
     */
    @Select("SELECT t1.* FROM tb_brand t1 JOIN tb_category_brand t2 ON t2.brand_id = t1.id " +
            "JOIN tb_category t3 ON t2.category_id = t3.id WHERE t3.name = #{name}")
    List<Brand> findByCateName(@Param("name") String cateName);
}
