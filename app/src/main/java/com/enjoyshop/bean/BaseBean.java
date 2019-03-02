package com.enjoyshop.bean;

import java.io.Serializable;

/**
 * <pre>

 *     desc   :model的基类
 *     version: 1.0
 * </pre>
 */

public class BaseBean implements Serializable {

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
