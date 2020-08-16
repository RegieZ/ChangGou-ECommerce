package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    private EsSearchService esSearchService;

    /**
     * 多条件搜索
     *
     * @param searchMap
     * @return
     */
    @GetMapping("list")
    public Result search(@RequestParam Map<String, String> searchMap) {
        Map<String, Object> resultMap = esSearchService.search(searchMap);
        return new Result("搜索成功", resultMap);
    }
}
