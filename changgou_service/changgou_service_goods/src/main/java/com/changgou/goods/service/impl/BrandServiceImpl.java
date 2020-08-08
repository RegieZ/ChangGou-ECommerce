package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.service.BrandService;
import com.changgou.pojo.Brand;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询所有品牌
     *
     * @return
     */
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Brand findById(Integer id) {
        //模拟异常
        if (id == 0) {
            throw new RuntimeException("ID不能为0");
        }
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加
     *
     * @param brand
     */
    @Override
    public void add(Brand brand) {
        //brandMapper.insert(brand); //全部字段参与的sql拼接
        brandMapper.insertSelective(brand); //有值参与的sql拼接
    }

    /**
     * 修改
     *
     * @param brand
     */
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Brand> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return brandMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        // select * from tb_brand where name like %name% and letter = letter
        // 构建查询对象
        Example example = new Example(Brand.class);
        // 用于封装查询条件
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // id
            String id = (String) searchMap.get("id"); // 表中Integer类型的也用String接收
            if (id != null) {
                criteria.andEqualTo("id", id);
            }
            // 品牌名称
            // 这两个条件用StringUtils.isNotEmpty(name)也可以判断
            String name = (String) searchMap.get("name");
            if (name != null && !"".equals(name)) {
                criteria.andLike("name", "%" + name + "%");
            }
            // 品牌的首字母
            String letter = (String) searchMap.get("letter");
            if (letter != null && !"".equals(letter)) {
                criteria.andEqualTo("letter", letter);
            }
        }
        return example;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Brand> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Brand>) brandMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Brand> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Brand>) brandMapper.selectByExample(example);
    }

}
