package com.west2ol.april.entity.receive;

public class LoginInfo {

    /**
     * status : 0
     * uid : 1
     * token : f8cafa6c-cfb9-468f-ad9a-f0148d3f6314
     */

    private int status;
    private int uid;
    private String token;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
