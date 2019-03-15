package com.sevenshop.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lenovo on 2019/3/14.
 */

public class Collect extends BmobObject {
    private User collecter;
    private Goods goods;

    public Goods getGoods() {
        return goods;
    }

    public User getCollecter() {
        return collecter;
    }

    public void setCollecter(User collecter) {
        this.collecter = collecter;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

}
