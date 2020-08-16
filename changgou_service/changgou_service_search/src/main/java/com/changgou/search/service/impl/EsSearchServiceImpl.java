package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.EsSearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class EsSearchServiceImpl implements EsSearchService {

    @Autowired
    private ElasticsearchTemplate template;

    /**
     * @Date: 11:18 2020/8/8
     * @Param: [searchMap]
     * @return: com.changgou.entity.Result
     * @Description: 多条件搜索
     **/
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(searchMap)) {
            //设置查询组合条件对象 must should must not
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            /*
                需求一：根据关键字查询(name-->手机)
             */
            String keywords = searchMap.get("keywords");
            if (StringUtils.isNotEmpty(keywords)) {
                //match 默认会分词并且是or查询，此处设置为and查询
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords).operator(Operator.AND));
            }

            //原生查询对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
            /*
                需求二： 品牌列表分组
                       设置参数：
                        1.组名
                        2.分组条件
             */
            String brandGroup = "brandGroup"; //组名
            //设置分组组名+分组字段
            TermsAggregationBuilder brandGroupAgg = AggregationBuilders.terms(brandGroup).field("brandName");
            nativeSearchQueryBuilder.addAggregation(brandGroupAgg);

            /*
                需求三：分类列表分组
                       设置参数：
                        1.组名
                        2.分组条件
             */
            String cateGroup = "cateGroup"; //组名
            TermsAggregationBuilder categoryNameGroupAgg = AggregationBuilders.terms(cateGroup).field("categoryName");
            nativeSearchQueryBuilder.addAggregation(categoryNameGroupAgg);

            /*
                需求四： 规格列表分组
                       设置参数：
                        1.组名
                        2.分组条件

             */
            String specGroup = "specGroup"; //组名
            /*
                es 中的text类型不可以分组聚合，但是可以临时转换为keyword参与分组聚合
             */
            TermsAggregationBuilder specGroupAgg = AggregationBuilders.terms(specGroup).field("spec.keyword");
            nativeSearchQueryBuilder.addAggregation(specGroupAgg);

            AggregatedPage<SkuInfo> aggregatedPage = template.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    //用于添加结果
                    List<T> list = new ArrayList<>();
                    SearchHits hits = response.getHits();
                    if (null != hits) {
                        SearchHit[] searchHits = hits.getHits();
                        for (SearchHit searchHit : searchHits) {
                            //查询结果--->skuinfo json串
                            String sourceAsString = searchHit.getSourceAsString();
                            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
                            list.add((T) skuInfo);
                        }
                        /*
                            第一个参数：查询列表skuList
                            第二个参数：分页对象
                            第三个参数：查询结果总数
                            第四个参数：分组结果
                         */
                        return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), response.getAggregations());
                    }
                    return new AggregatedPageImpl<T>(list);
                }
            });

            //解析
            List<SkuInfo> skuInfoList = aggregatedPage.getContent(); //查询结果
            long total = aggregatedPage.getTotalElements(); //总记录数
            int totalPages = aggregatedPage.getTotalPages(); //总页数

             /*
                需求二： 品牌列表分组解析
             */
            List<String> brandList = new ArrayList<>();//分组列表
            StringTerms brandTerms = (StringTerms) aggregatedPage.getAggregation(brandGroup);
            List<StringTerms.Bucket> brandTermsBuckets = brandTerms.getBuckets();
            for (StringTerms.Bucket bucket : brandTermsBuckets) {
                String brandName = bucket.getKeyAsString();
                brandList.add(brandName);
            }

            /*
                需求三：分类列表分组结果解析
             */
            List<String> categoryList = new ArrayList<>();
            StringTerms categoryStringTerms = (StringTerms) aggregatedPage.getAggregation(cateGroup);
            List<StringTerms.Bucket> cateStringTermsBuckets = categoryStringTerms.getBuckets();
            for (StringTerms.Bucket bucket : cateStringTermsBuckets) {
                String categoryName = bucket.getKeyAsString();
                categoryList.add(categoryName);
            }

            /*
                需求四： 规格列表分组结果解析

                "{'颜色': '蓝色', '版本': '6GB+128GB'}",
                "{'颜色': '黑色', '版本': '6GB+128GB'}",
                "{'颜色': '黑色', '版本': '4GB+64GB'}",
                "{'颜色': '蓝色', '版本': '4GB+64GB'}",
                "{'颜色': '蓝色', '版本': '6GB+64GB'}",
                "{'颜色': '黑色', '版本': '6GB+64GB'}",

                 颜色：黑色,蓝色
                版本：6GB+128GB，4GB+64GB,6GB+64GB
             */

            //错误方式
         /*   List<String> specList = new ArrayList<>();
            StringTerms specStringTerms = (StringTerms) aggregatedPage.getAggregation(specGroup);
            List<StringTerms.Bucket> specBuckets = specStringTerms.getBuckets();
            for (StringTerms.Bucket bucket : specBuckets) {
                String spec = bucket.getKeyAsString();
                specList.add(spec);
            }*/
            //正确使用姿势
            Map<String, Set<String>> specList = new HashMap<>();

            StringTerms specStringTerms = (StringTerms) aggregatedPage.getAggregation(specGroup);
            List<StringTerms.Bucket> specBuckets = specStringTerms.getBuckets();
            for (StringTerms.Bucket bucket : specBuckets) {
                // "{'颜色': '黑色', '版本': '6GB+64GB'}"
                String spec = bucket.getKeyAsString();
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (specList.containsKey(key)) { //之前已经存在key值，将set列表获取出来添加
                        Set<String> set = specList.get(key);
                        set.add(value);
                    } else {
                        Set<String> set = new HashSet<>();
                        set.add(value);
                        specList.put(key, set);
                    }
                }
            }
            //将结果添加到resultMap中
            resultMap.put("rows", skuInfoList);
            resultMap.put("total", total);
            resultMap.put("totalPages", totalPages);
            resultMap.put("brandList", brandList); //品牌列表
            resultMap.put("cateList", categoryList); //分类列表
            resultMap.put("specList", specList); //规格列表
        }

        return resultMap;
    }
}
