package com.oracle.bean;

import com.alibaba.fastjson.JSONObject;

public class GetTransactionRes {
    private String id;
    private String block_time;
    private String block_num;
    private String last_irreversible_block;
    private JSONObject trx;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlock_time() {
        return block_time;
    }

    public void setBlock_time(String block_time) {
        this.block_time = block_time;
    }

    public String getBlock_num() {
        return block_num;
    }

    public void setBlock_num(String block_num) {
        this.block_num = block_num;
    }

    public String getLast_irreversible_block() {
        return last_irreversible_block;
    }

    public void setLast_irreversible_block(String last_irreversible_block) {
        this.last_irreversible_block = last_irreversible_block;
    }

    public JSONObject getTrx() {
        return trx;
    }

    public void setTrx(JSONObject trx) {
        this.trx = trx;
    }
}
