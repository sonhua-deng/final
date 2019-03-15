package com.sevenshop.bean;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lenovo on 2019/3/7.
 */

public class Goods extends BmobObject {
    List<String> photoUrls;
    String title;
    String des;
    int startPrice;
    int currenPrice;
    String type;
    String shellType;
    User pulisher;
    User buyer;

    public boolean isShell() {
        return isShell;
    }

    boolean isShell;

    @Override
    public String toString() {
        String result=title+des+type+shellType;
        if (pulisher!=null &&pulisher.getNickName()!=null) {
            result+=pulisher.getNickName();
        }
        if (buyer!=null &&buyer.getNickName()!=null) {
            result+=buyer.getNickName();
        }
        return result;
    }
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTitle() {
        return title;
    }

    public int getCurrenPrice() {
        return currenPrice;
    }

    public int getStartPrice() {
        return startPrice;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public String getShellType() {
        return shellType;
    }

    public String getType() {
        return type;
    }

    public User getBuyer() {
        return buyer;
    }

    public User getPulisher() {
        return pulisher;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCurrenPrice(int currenPrice) {
        this.currenPrice = currenPrice;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setPulisher(User pulisher) {
        this.pulisher = pulisher;
    }

    public void setShell(boolean shell) {
        isShell = shell;
    }

    public void setShellType(String shellType) {
        this.shellType = shellType;
    }

    public void setStartPrice(int startPrice) {
        this.startPrice = startPrice;
    }

    public void setType(String type) {
        this.type = type;
    }


}
