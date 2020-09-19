package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Page;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <dl>
 * <dd>描述: ~节点业务逻辑实现</dd>
 * <dd>创建时间：  11:20 2020/8/8</dd>
 * <dd>创建人： zz</dd>
 * <dt>版本历史: </dt>
 * <pre>
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * 2020/8/8      guodong       1.0        1.0 Version
 * </pre>
 * </dl>
 */
@Service
public class EsSearchServiceImpl implements EsSearchService {

    @Autowired
    private ElasticsearchTemplate template;

    private Logger log = LoggerFactory.getLogger(EsSearchServiceImpl.class);

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
                需求一：根据关键字查询---》name-->手机
             */
            String keywords = searchMap.get("keywords");
            if (StringUtils.isNotEmpty(keywords)) {
                //match 默认会分词并且是or查询，此处设置为and查询
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords).operator(Operator.AND));
            }

            /*
                需求五： 根据品牌精确查询---》term
             */
            String brand = searchMap.get("brand");
            if(StringUtils.isNotEmpty(brand)){
                /*
                    must: 带评分模型的查询---》score
                    filter: 不带评分模型的查询
                 */

                boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", brand));
            }

            /*
                需求六：根据分类名称精确查询
             */
            String category = searchMap.get("categoryName");
            if(StringUtils.isNotEmpty(category)){
                boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", category));
            }

            /*
                需求七：根据规格精确查询

                前端传输：
                    spec_颜色： 红色
                    spec_内存： 4g
                    spec_....: xx
                es中字段名：
                    specMap.颜色： 红色
                    specMap.内存： 4g
                    specMap.....： xx
             */
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if(key.startsWith("spec_")){
                    String specKey = key.replace("spec_", "specMap.") + ".keyword";
                    log.info("替换后的规格key: {}", specKey);
                    boolQueryBuilder.filter(QueryBuilders.termQuery(specKey, value));
                }
            }

            /*
                根据价格范围查询
                    price: 200-300
             */
            String price = searchMap.get("price");
            if(StringUtils.isNotEmpty(price)){
                String[] split = price.split("-");
                if(2==split.length){
                    String beginPrice = split[0]; //开始价格
                    String endPrice = split[1]; //结束价格
                    /*
                        gt: 大于
                        gte：大于等于
                        lt: 小于
                        lte：小于等于
                     */
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(beginPrice).lte(endPrice));
                }
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

            /*
                需求八：排序
                    sortFiled: 排序字段
                    sortRule: 排序规则
             */

            String sortFiled = searchMap.get("sortFiled");
            String sortRule = searchMap.get("sortRule");
            if(StringUtils.isNotEmpty(sortFiled) && StringUtils.isNotEmpty(sortRule)){
                if("DESC".equalsIgnoreCase(sortRule)){
                    FieldSortBuilder sortBuilder = SortBuilders.fieldSort(sortFiled).order(SortOrder.DESC); //倒排
                    nativeSearchQueryBuilder.withSort(sortBuilder);
                }else {
                    FieldSortBuilder sortBuilder = SortBuilders.fieldSort(sortFiled).order(SortOrder.ASC); //正排
                    nativeSearchQueryBuilder.withSort(sortBuilder);
                }
            }

            /*
                需求十：分页
                    页大小：
                    页码：

             */
            int pageNum = Page.pageNum; //默认页码
            int pageSize = Page.pageSize; //默认页大小
            String pageNumStr = searchMap.get("pageNum"); //页码
            String pageSizeStr = searchMap.get("pageSize"); //页大小
            if(StringUtils.isNotEmpty(pageNumStr)){
                pageNum = Integer.valueOf(pageNumStr);
            }
            if(StringUtils.isNotEmpty(pageSizeStr)){
                pageSize = Integer.parseInt(pageSizeStr);
            }
            //此处需要注意：0:表示第一页 1：表示第二页 以此类推...
            PageRequest pageAble = PageRequest.of(pageNum-1, pageSize);
            nativeSearchQueryBuilder.withPageable(pageAble);

            /*
                需求十一： 高亮
                    1.高亮字段---->name
                    2.前置标签 <font color='red'>  或者 <span style='color:red'>
                    3.后置标签  </font> 或者 </span>
             */
            /*HighlightBuilder highlightFields = new HighlightBuilder()
                    .field("name")
                    .preTags("")
                    .postTags("");*/
            //设置高亮查询条件
            HighlightBuilder.Field highlightFields = new HighlightBuilder
                    .Field("name")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            nativeSearchQueryBuilder.withHighlightFields(highlightFields);
            AggregatedPage<SkuInfo> aggregatedPage = template.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    //用于添加结果
                    List<T> list = new ArrayList<>();
                    SearchHits hits = response.getHits();
                    if (null != hits) {
                        SearchHit[] searchHits = hits.getHits();
                        for (SearchHit searchHit : searchHits) {
                            //查询结果---》skuinfo json串
                            String sourceAsString = searchHit.getSourceAsString();
                            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);

                            /*
                                需求十一： 高亮解析
                             */
                            Map<String, HighlightField> hitHighlightFields = searchHit.getHighlightFields();
                            if (!CollectionUtils.isEmpty(hitHighlightFields)) {
                                String name = hitHighlightFields.get("name").getFragments()[0].toString();
                                skuInfo.setName(name);
                            }
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
                    if(specList.containsKey(key)){ //之前已经存在key值，将set列表获取出来添加
                        Set<String> set = specList.get(key);
                        set.add(value);
                    }else {
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