package com.oracle.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.oracle.bean.EosNodeTableRequest;
import com.oracle.bean.GetBlockReq;
import com.oracle.bean.GetTransactionReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value="classpath:/application.properties")
public class NodeosApiService {

    @Value("${eos.url}")
    String baseUrl;

    public String getTable(EosNodeTableRequest body){
        String url = baseUrl+  "/v1/chain/get_table_rows";
        String sendStr = new Gson().toJson(body);
        JsonParser jsonParser = new JsonParser();
        String bodyReaded = HttpRequest.post(url).send(sendStr).body();
        return bodyReaded;
    }

    public String getTransaction(String id){
        GetTransactionReq getTransactionRequest = new GetTransactionReq();
        getTransactionRequest.setId(id);
        String bodyReaded = HttpRequest.post(baseUrl+"/v1/history/get_transaction").send(new Gson().toJson(getTransactionRequest)).body();
        return bodyReaded;
    }


    public String getBlockInfo(Long blockNum){
        GetBlockReq req = new GetBlockReq();
        req.setBlock_num_or_id(""+blockNum);
        String bodyReaded = HttpRequest.post(baseUrl+"/v1/chain/get_block").send(new Gson().toJson(req)).body();
        return bodyReaded;
    }

}
