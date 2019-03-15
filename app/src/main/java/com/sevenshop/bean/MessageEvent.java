package com.sevenshop.bean;

/**
 * Describe: eventbus 的数据模型
 */

public class MessageEvent {

    public MessageEvent(int type) {
        this.type = type;
    }

    private int type = 0;

    private int money;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
