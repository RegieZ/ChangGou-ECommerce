package com.changgou.search.service;

import java.util.Map;

public interface EsSearchService {

    /**
     * 多条件搜索
     *
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String, String> searchMap);
}
