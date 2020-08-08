package com.changgou.goods.controller;

import com.changgou.entity.PageResult;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.service.AlbumService;
import com.changgou.pojo.Album;
import com.changgou.pojo.Brand;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping("findAll")
    public Result findAll(){
        List<Album> albumList = albumService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",albumList);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Album album = albumService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", album);
    }

    @PostMapping("add")
    public Result add(@RequestBody Album album) {
        albumService.add(album);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @PutMapping(value = "/update/{id}")
    public Result update(@RequestBody Album album, @PathVariable Integer id) {
        album.setId(id);
        albumService.update(album);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Integer id) {
        albumService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @GetMapping(value = "/search/{page}/{size}")
    public Result findPage(@RequestParam Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Brand> pageList = albumService.findPage(searchMap, page, size);
        PageResult pageResult = new PageResult(pageList.getTotal(), pageList.getResult());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }
}
