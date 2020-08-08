package com.changgou.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_album")
public class Album {

    @Id
    private Integer id;
    private String title;
    private String image;
    private String image_items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_items() {
        return image_items;
    }

    public void setImage_items(String image_items) {
        this.image_items = image_items;
    }
}
