package com.example.myapplication.bean;

import java.io.Serializable;

public class AiImgFinalList implements Serializable {
    private String img_approve_conclusion;
    private String img_url;
    private int width;
    private int height;

    public String getImg_approve_conclusion() {
        return img_approve_conclusion;
    }

    public void setImg_approve_conclusion(String img_approve_conclusion) {
        this.img_approve_conclusion = img_approve_conclusion;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
