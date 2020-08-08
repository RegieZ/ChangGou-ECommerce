package com.changgou.goods.service;

import com.changgou.pojo.Album;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface AlbumService {

    public List<Album> findAll();

    public Album findById(Integer id);

    public void add(Album album);

    public void update(Album album);

    public void delete(Integer id);

    Page<Album> findPage(Map<String, Object> searchMap, int page, int size);
}
