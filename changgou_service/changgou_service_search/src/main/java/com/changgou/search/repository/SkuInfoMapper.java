package com.changgou.search.repository;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuInfoMapper extends ElasticsearchRepository<SkuInfo, Long> {

}