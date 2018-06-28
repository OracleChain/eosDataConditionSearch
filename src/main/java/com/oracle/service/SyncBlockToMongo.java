package com.oracle.service;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oracle.config.Config;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Service
public class SyncBlockToMongo {
    @Autowired
    NodeosApiService nodeosApiService;

    @Autowired
    Config config;

    private final Logger log = LoggerFactory.getLogger(SyncBlockToMongo.class);

    public static MongoClient mongoClient=null;
    public static MongoDatabase mongoDatabase = null;
    public static MongoCollection<Document> collection = null;
    public MongoCollection<Document> getMongoDatabase()
    {
        if(null == mongoClient) {
            ServerAddress serverAddress = new ServerAddress(config.getMongoIP(),config.getMongoPort());
            MongoCredential credential = MongoCredential.createScramSha1Credential(config.getMongoUsername(), config.getMongoDatabase(), config.getMongoPassword().toCharArray());


            MongoClientOptions options = MongoClientOptions.builder()
                    .writeConcern(WriteConcern.JOURNALED)
                    .build();
            mongoClient = new MongoClient(serverAddress,credential, options);

        }
        // 连接到数据库
        if(null == mongoDatabase) {
            mongoDatabase = mongoClient.getDatabase("eos");
        }
        log.info("Connect to database successfully");

        if(null == collection) {
            collection = mongoDatabase.getCollection("blocks");
        }
        log.info("getCollection successfully");

        return collection;
    }
    public Long getCurrentMainNetHeadBlock(){
        String response = HttpRequest.get(config.getBaseUrl()+"/v1/chain/get_info").body();
        JSONObject jo = new Gson().fromJson(response, JSONObject.class);
        Long headBlockNum = jo.getLong("head_block_num");
        return headBlockNum;
    }

    public boolean syncMongo(Long bn) throws Exception {

        try{
            // 连接到 mongodb 服务
            String blockNum = ""+bn;
            Document docFindPre = getMongoDatabase().find(eq("block_num", bn)).first();
            if (docFindPre != null){
                return true;
            }
            log.info("==================> Real ---StartSyncBlock"+bn);


            //插入文档
            /**
             * 1. 创建文档 org.bson.Document 参数为key-value的格式
             * 2. 创建文档集合List<Document>
             * 3. 将文档集合插入数据库集合中 mongoCollection.insertMany(List<Document>) 插入单个文档可以用 mongoCollection.insertOne(Document)
             * */
            String data = nodeosApiService.getBlockInfo(bn);
            if(data.indexOf("producer") == -1){
                throw new Exception(data);
            }

            Document document = Document.parse(data);
            List<Document> documents = new ArrayList<Document>();
            documents.add(document);

            docFindPre = getMongoDatabase().find(eq("block_num", bn)).first();
            if (docFindPre != null){
                return true;
            }
            collection.insertMany(documents);

        }catch(Exception e){
            log.info( e.getClass().getName() + ": " + e.getMessage());
            return false;
        }

        return true;
    }



    public  static int n=0;
    public void StartSyncBlock(Long startBlockNum){

        if(n>0){
            return;
        }
        n++;

        while(true) {
            try {
                if(!syncMongo(startBlockNum)){
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){
                        log.error(e.toString());
                    }
                    continue;
                }
            }catch (Exception e){
                log.error(e.toString());
                continue;
            }
            startBlockNum++;
        }
    }
}
