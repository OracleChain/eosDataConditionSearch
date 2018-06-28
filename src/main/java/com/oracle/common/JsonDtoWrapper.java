package com.oracle.common;

import java.io.Serializable;

public class JsonDtoWrapper<DTO> implements Serializable {
    private String code;
    private String msg;
    private DTO data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeMsg(APIResultCode cm){
        this.code = cm.getCode();
        this.msg = cm.getMessage();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DTO getData() {
        return data;
    }

    public void setData(DTO data) {
        this.data = data;
    }
}