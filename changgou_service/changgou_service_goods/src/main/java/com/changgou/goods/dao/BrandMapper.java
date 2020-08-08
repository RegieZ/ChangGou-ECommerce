package com.changgou.goods.dao;

import com.changgou.pojo.Brand;
import tk.mybatis.mapper.common.Mapper;

//mybatis用@mapper扫包，tk-mybatis在GoodsApplication中声明，这里类似spring data es中的repository
public interface BrandMapper extends Mapper<Brand> {
}
