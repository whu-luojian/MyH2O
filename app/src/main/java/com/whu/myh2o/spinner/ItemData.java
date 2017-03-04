package com.whu.myh2o.spinner;

/**
 * Created by luojian on 2016/9/11.
 */
public class ItemData {
    private final String text;
    private final Integer imageId;

    public ItemData(String text, Integer imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public Integer getImageId() {
        return imageId;
    }
}
