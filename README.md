## 1. EOS itself does not support get transaction by id, and get actions operation (as shown below). eosDataConditionSearch supports partial query of these two functions in advance (transfer related actions query)  
eg.  
cleos -u http://mainnet.genereos.io  get transaction ec6887d35adfefc9d7443189d9802cdf3b9de24e065f887399827c7e3bf3ffdd  
**There is a problem with the results as below, block_time is "2000-..."**  
{  
  "id": "ec6887d35adfefc9d7443189d9802cdf3b9de24e065f887399827c7e3bf3ffdd",  
  "trx": null,  
  "block_time": "2000-01-01T00:00:00.000",  
  "block_num": 0,  
  "last_irreversible_block": 3045942,  
  "traces": []  
}  
  
eg.  
cleos -u http://mainnet.genereos.io  get actions helloworldgo  
but get nothing as below  
seq  when                              contract::action => receiver      trx id...   args  

## 2. This program provides eos gettransactionbyid and getactions about transfer and transferfrom (contracts Must meet the eosio.token standard,for example eosdactoken of oraclechain) functionality   

### 2.1 Install mongodb,create database eos with your username and password  

### 2.2 open eosDataConditionSearch with Intellij, Then config java application file: application.properties  
eos.url=youeosnode  
mongo.ip=  
mongo.port=27017  
mongo.username=  
mongo.password=  
mongo.database=eos  
...  

### 2.3 compile with maven, Deploy war package to tomcat,and then StartSyncBlock from latest block number，sync bolck before, you need to simply modify the program and start syncing from the first block  
curl "http://127.0.0.1:8080/VX/StartSyncBlock"  

### 2.4 Now,you can try GetTransactionById and GetActions  
curl "http://127.0.0.1:8080/VX/GetTransactionById/ec6887d35adfefc9d7443189d9802cdf3b9de24e065f887399827c7e3bf3ffdd"  
curl "http://127.0.0.1:8080/VX/GetActions"  -X POST -d '{"symbols":[{"symbolName":"AAA","contractName":"helloworldgo"},{"symbolName":"EOS","contractName":"eosio.token"}],"from":"prizequiz233", "to":"", "page":0, "pageSize":30}' -H "Content-Type: application/json"  

### 2.5 Create index  
##### You must create an index, otherwise it will be slow，After synchronizing several blocks, create another index  
use eos;  
db.auth(username, password);  
db.blocks.createIndex({"block_num":1})  
db.blocks.createIndex({"transactions.trx.id":1})  
db.blocks.createIndex({"transactions.trx.transaction.actions.account":1})
