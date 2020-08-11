package com.changgou.goods.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tb_category_brand")
public class CategoryBrand implements Serializable {

    //不建议使用基本int类型，由于默认值是0，建议使用包装类Integer
    @Id
    private Integer category_id; //驼峰命名categoryId也可以解析出来，不过有时会出错，需要修改SpuServiceImpl中的变量

    @Id
    private Integer brand_id; //驼峰命名brandId也可以解析出来，不过有时会出错，需要修改SpuServiceImpl中的变量

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public Integer getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(Integer brand_id) {
        this.brand_id = brand_id;
    }
}