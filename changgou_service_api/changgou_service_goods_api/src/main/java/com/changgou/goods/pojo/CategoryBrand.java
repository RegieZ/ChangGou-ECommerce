package com.changgou.goods.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tb_category_brand")
public class CategoryBrand implements Serializable {

    @Id
    private Integer category_id; //categoryId也可以解析出来，不过有时会出错，需要修改SpuServiceImpl中的变量

    @Id
    private Integer brand_id; //brandId也可以解析出来，不过有时会出错，需要修改SpuServiceImpl中的变量

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