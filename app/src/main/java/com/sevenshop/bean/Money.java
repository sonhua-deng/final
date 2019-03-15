package com.sevenshop.bean;

import java.io.Serializable;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;

/**
 * Created by Lenovo on 2019/3/14.
 */

public class Money extends BmobObject implements Serializable {
    private User  user;
    private int  money;

    public int getMoney() {
        return money;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
