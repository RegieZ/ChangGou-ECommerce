package com.changgou.goods.service.impl;

import com.changgou.goods.dao.AlbumMapper;
import com.changgou.goods.service.AlbumService;
import com.changgou.pojo.Album;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    @Override
    public Album findById(Integer id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Album album) {
        albumMapper.insertSelective(album);
    }

    @Override
    public void update(Album album) {
        albumMapper.updateByPrimaryKeySelective(album);
    }

    @Override
    public void delete(Integer id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Page<Album> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Album>) albumMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            String id = (String) searchMap.get("id");
            if (id != null) {
                criteria.andEqualTo("id", id);
            }
            String title = (String) searchMap.get("title");
            if (title != null && !"".equals(title)) {
                criteria.andLike("title", "%" + title + "%");
            }
            String image = (String) searchMap.get("image");
            if (image != null && !"".equals(image)) {
                criteria.andEqualTo("image", image);
            }
        }
        return example;
    }

}
