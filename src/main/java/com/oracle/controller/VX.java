/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oracle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import com.oracle.bean.*;
import com.oracle.common.APIResultCode;
import com.oracle.common.JsonDtoWrapper;
import com.oracle.service.NodeosApiService;
import com.oracle.service.SyncBlockToMongo;
import org.apache.http.util.TextUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.oracle.common.APIResultCode.TRANSACTION_NOT_EXIST;

@Controller
public class VX {
	private final Logger log = LoggerFactory.getLogger(VX.class);


	@Autowired
	NodeosApiService nodeosApiService;

	@Autowired
	SyncBlockToMongo syncBlockToMongo;

	@PostMapping("/GetActions")
	public ResponseEntity<JsonDtoWrapper<Object>> GetActions(@RequestBody GetActionsReq getActionsReq){

	int indexFrom = getActionsReq.getPage()*getActionsReq.getPageSize();
	int limit = getActionsReq.getPageSize();


	List<Bson> accountList = new ArrayList<>();
	List<Bson> symbolList = new ArrayList<>();
	List<Bson> fromList = new ArrayList<>();
	Iterator<ContractSymbol> itrSymbol = getActionsReq.getSymbols().iterator();

	List<String> accountStrList = new ArrayList<>();
	List<String> symbolStrList = new ArrayList<>();
	List<String> contractStrList = new ArrayList<>();
	List<String> opNameStrList = new ArrayList<>();
	opNameStrList.add("transfer");
	opNameStrList.add("transferfrom");

	while (itrSymbol.hasNext()){
		ContractSymbol contractSymbol = itrSymbol.next();
		accountList.add(eq("transactions.trx.transaction.actions.account", contractSymbol.getContractName()));
		contractStrList.add(contractSymbol.getContractName());
		symbolList.add(regex("transactions.trx.transaction.actions.data.quantity", contractSymbol.getSymbolName()+"$"));
		symbolStrList.add(contractSymbol.getSymbolName());
	}

	if(!TextUtils.isEmpty(getActionsReq.getFrom())){
		fromList.add(eq("transactions.trx.transaction.actions.data.from", getActionsReq.getFrom()));
		accountStrList.add(getActionsReq.getFrom());
	}

	if(!TextUtils.isEmpty(getActionsReq.getTo())){
		fromList.add(eq("transactions.trx.transaction.actions.data.to", getActionsReq.getTo()));
		accountStrList.add(getActionsReq.getTo());
	}

	MongoCursor<Document> findIterable = syncBlockToMongo.getMongoDatabase().find(and(
			or(accountList),
			or(eq("transactions.trx.transaction.actions.name", "transfer"), eq("transactions.trx.transaction.actions.name", "transferfrom")),
			or(fromList),
			or(symbolList))).sort(Sorts.descending("block_num")).skip(indexFrom).limit(limit).iterator();

		Set<String> setBlockNum = new HashSet<>();
		List<DocumentWithBlock> docActionsRef = new ArrayList<>();
		while(findIterable.hasNext()){
			Document docBlock = findIterable.next();//.trx.transaction.actions

			if(setBlockNum.contains(""+docBlock.get("block_num"))){
				continue;
			}else{
				setBlockNum.add(""+docBlock.get("block_num"));
			}

			ArrayList<Document> docTransactions = (ArrayList<Document>) docBlock.get("transactions");
			for (Document docTrans:docTransactions){
				ArrayList<Document> actions = new ArrayList<>();
				try {
					actions = docTrans.get("trx", Document.class).get("transaction", Document.class).get("actions", actions.getClass());
					for (Document action:actions){
						if(!contractStrList.contains(action.get("account"))){
							continue;
						}

						if (!opNameStrList.contains(action.get("name"))){
							continue;
						}

						if (!(accountStrList.contains(action.get("data", Document.class).get("from"))||accountStrList.contains(action.get("data", Document.class).get("to")))){
							continue;
						}

						String quantity = (String)action.get("data", Document.class).get("quantity");
						int indexSpace = quantity.indexOf(" ");
						String symbol = quantity.substring(indexSpace+1, quantity.length());
						if (!symbolStrList.contains(symbol)){
							continue;
						}
						DocumentWithBlock docWithBlock = new DocumentWithBlock();
						docWithBlock.setDoc(action);
						String bl = ""+docBlock.get("block_num");
						Double d = Double.parseDouble(bl);
						Long l = d.longValue();
						docWithBlock.setBlockNum(l);
						docWithBlock.setTrxid(docTrans.get("trx", Document.class).get("id", String.class));

						docActionsRef.add(docWithBlock);
					}
				}catch (Exception e){
					log.error(e.toString());
				}
			}
		}


		JsonDtoWrapper<Object> jo = new JsonDtoWrapper<>();
		GetActionRes getActionRes = new GetActionRes();
		getActionRes.setActions(docActionsRef);
		getActionRes.setPage(getActionsReq.getPage());
		getActionRes.setPageSize(getActionsReq.getPageSize());

		MongoCursor<Document> findIterableHasMore = syncBlockToMongo.getMongoDatabase().find(and(
				or(accountList),
				or(eq("transactions.trx.transaction.actions.name", "transfer"), eq("transactions.trx.transaction.actions.name", "transferfrom")),
				or(fromList),
				or(symbolList))).sort(Sorts.descending("block_num")).skip(indexFrom+limit).limit(1).iterator();
		if(findIterableHasMore.hasNext()){
			getActionRes.setHasMore(true);
		}else{
			getActionRes.setHasMore(false);
		}



		jo.setCodeMsg(APIResultCode.SUCCESS);
		jo.setData(getActionRes);
		return ResponseEntity.ok().body(jo);
	}

	public JSONObject getTransactionOnLine(long bn, String trxid){
		String data = nodeosApiService.getBlockInfo(bn);

		JSONObject jo = new Gson().fromJson(data, JSONObject.class);
		JSONObject  joTransactionArrIndex = null;

		if(null != jo.get("transactions")){
			JSONArray jTrx = jo.getJSONArray("transactions");
			if(null != jTrx && jTrx.size()>0){
				for(int i=0; i<jTrx.size(); i++) {
					joTransactionArrIndex = (JSONObject) jTrx.get(i);
					JSONObject joTrx = joTransactionArrIndex.getJSONObject("trx");
					if (null != joTrx &&
							trxid.equals(joTrx.getString("id"))) {
						JSONObject joTransaction = joTrx.getJSONObject("transaction");
						return joTransaction;
					}
				}
			}
		}
		return null;
	}

	@GetMapping("/GetTransactionById/{someID}")
	public ResponseEntity<JsonDtoWrapper<Object>> getBpJson(@PathVariable(value = "someID") String id){

		JsonDtoWrapper<Object> jo = new JsonDtoWrapper<>();
		try {
			Document doc = syncBlockToMongo.getMongoDatabase().find(eq("transactions.trx.id", id)).first();
			GetTransactionRes getTransactionRes = new GetTransactionRes();
			JSONObject joTrx = null;
			if(null != doc && null != doc.get("block_num") ){
				String bl = ""+doc.get("block_num");
				Double d = Double.parseDouble(bl);
				Long l = d.longValue();
				joTrx = getTransactionOnLine(Long.parseLong(""+l), id);
			}

			if(joTrx != null){
				getTransactionRes.setBlock_num(""+doc.get("block_num"));
				getTransactionRes.setTrx(joTrx);
				jo.setCodeMsg(APIResultCode.SUCCESS);
				jo.setData(getTransactionRes);
			}else{
				jo.setCodeMsg(TRANSACTION_NOT_EXIST);
			}
		}catch (Exception e){
			jo.setCodeMsg(TRANSACTION_NOT_EXIST);
		}

		return ResponseEntity.ok().body(jo);
	}


	@GetMapping("/StartSyncBlock/{someID}")
	public ResponseEntity<JsonDtoWrapper<Object>> StartSyncBlock(@PathVariable(value = "someID") String id){
		try {
			syncBlockToMongo.syncMongo(Long.parseLong(id));
		}catch (Exception e){
			log.error(e.toString());
		}

		JsonDtoWrapper<Object> jo = new JsonDtoWrapper<>();
		jo.setCodeMsg(APIResultCode.SUCCESS);
		return ResponseEntity.ok().body(jo);
	}

	@GetMapping("/StartSyncBlock")
	public ResponseEntity<JsonDtoWrapper<Object>> StartSyncBlock(){
		Long startBlockNum = syncBlockToMongo.getCurrentMainNetHeadBlock();
		syncBlockToMongo.StartSyncBlock(startBlockNum);
		JsonDtoWrapper<Object> jo = new JsonDtoWrapper<>();
		jo.setCodeMsg(APIResultCode.SUCCESS);
		return ResponseEntity.ok().body(jo);
	}
}
