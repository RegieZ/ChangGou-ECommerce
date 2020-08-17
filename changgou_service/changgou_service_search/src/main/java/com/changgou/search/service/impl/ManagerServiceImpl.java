package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.repository.SkuInfoMapper;
import com.changgou.search.service.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ManagerServiceImpl implements ManagerService {

    private Logger log = LoggerFactory.getLogger(ManagerServiceImpl.class);
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SkuInfoMapper skuInfoMapper;

    /**
     * 导入历史数据
     */
    @Override
    public void importAll() {
        //1.通过feign接口查询所有数据--->list<sku>
        List<Sku> skuList = skuFeign.findAll();
        long begin = System.currentTimeMillis(); //开始时间
        saveSkuList(skuList, begin);
    }

    /**
     * 根据spuId导入sku数据成功
     *
     * @param spuId
     */
    @Override
    public void importBySpuId(String spuId) {
        //根据spuId查询skuList
        List<Sku> skuList = skuFeign.findBySpuId(spuId);
        //保存
        saveSkuList(skuList, System.currentTimeMillis());
    }

    /**
     * 根据spuId删除sku索引库数据
     *
     * @param spuId
     */
    @Override
    public void deleteBySpuId(String spuId) {
        skuInfoMapper.deleteBySpuId(spuId);
    }

    /**
     * 保存skuList
     *
     * @param skuList
     * @param begin
     */
    public void saveSkuList(List<Sku> skuList, long begin) {
        log.info("查询到skuList大小：{}", skuList.size());
        //2.将list<sku>转换为---> List<skuinfo>
        String jsonString = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonString, SkuInfo.class);
        //3.特殊处理specMap
        /*
            spec: {"颜色":"红色","内存":"8g"}---> {}
         */
        for (SkuInfo skuInfo : skuInfoList) {
            //spec: {"颜色":"红色","内存":"8g"}
            String spec = skuInfo.getSpec();
            Map specMap = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(specMap);
        }
        //4.保存
        skuInfoMapper.saveAll(skuInfoList);
        long end = System.currentTimeMillis(); //结束时间
        log.info("花费时间： {}", (end - begin));
    }
}
