
package com.sevenshop.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**

 * Describe:  用户信息
 */

public class User extends BmobUser implements Serializable {

    private Long   id;
    private String logo_url;
    private String mobi;
    private String nickName;
    private  String sexy;
    private String hometown;
    private String signature;

    public String getHometown() {
        return hometown;
    }

    public String getSexy() {
        return sexy;
    }

    public String getSignature() {
        return signature;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public void setSexy(String sexy) {
        this.sexy = sexy;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public  String getNickName() {
        return nickName;
    }

    public  void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getUsername() {
        return super.getUsername();
    }

    public void setUsername(String username) {
        super.setUsername(username);
    }

    public String getMobi() {
        return mobi;
    }

    public void setMobi(String mobi) {
        this.mobi = mobi;
    }
}
