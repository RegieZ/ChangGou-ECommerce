package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /**
     * 查询所有sku
     * @return
     */
    @GetMapping
    List<Sku> findAll();
}