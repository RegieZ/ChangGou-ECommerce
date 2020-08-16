package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /**
     * 查询所有sku
     *
     * @return
     */
    @GetMapping
    List<Sku> findAll();

    /**
     * 根据spuId导入sku数据成功
     *
     * @param spuId
     */
    @GetMapping("findBySpuId/{spuId}")
    List<Sku> findBySpuId(@PathVariable("spuId") String spuId);
}