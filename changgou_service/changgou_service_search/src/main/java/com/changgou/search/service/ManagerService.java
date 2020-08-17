package com.changgou.search.service;

public interface ManagerService {

    /**
     * 导入历史数据
     */
    void importAll();

    /**
     * 根据spuId导入sku数据成功
     *
     * @param spuId
     */
    void importBySpuId(String spuId);

    /**
     * 根据spuId删除sku数据
     *
     * @param spuId
     */
    void deleteBySpuId(String spuId);
}