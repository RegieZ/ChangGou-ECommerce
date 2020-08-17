package com.changgou.search.repository;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SkuInfoMapper extends ElasticsearchRepository<SkuInfo, Long> {

    /**
     * 根据spuId删除sku数据
     *      底层语句转义：
     *          关键字+字段名+关键字+字段名...
     *          find, delete, ...
     *
     * @param spuId
     */
    List<SkuInfo> deleteBySpuId(String spuId);
}