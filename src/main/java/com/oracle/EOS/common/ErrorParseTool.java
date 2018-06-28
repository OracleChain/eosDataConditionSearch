package com.oracle.EOS.common;

import com.google.gson.Gson;
import com.oracle.EOS.bean.EosErrorRes;
import com.oracle.common.APIResultCode;
import org.apache.http.util.TextUtils;

public class ErrorParseTool {
    public static  CodeMessage parseMessage(String message){
        CodeMessage codeMessage = new CodeMessage();
        if (TextUtils.isEmpty(message)){
            return codeMessage;
        }
        try {
            EosErrorRes eosErrorRes = new Gson().fromJson(message, EosErrorRes.class);
            if (null != eosErrorRes) {
                String messageDetail = eosErrorRes.getError().getDetails().get(0).getMessage();
                int indexColon = messageDetail.indexOf(":");
                if (indexColon != -1){
                    String messageColon = messageDetail.substring(indexColon+1, messageDetail.length());
                    messageColon = messageColon.trim();
                    int indexSpace = messageColon.indexOf(" ");
                    if(org.apache.commons.lang3.StringUtils.isNumeric(messageColon.substring(0, indexSpace)))
                    {
                        codeMessage.setErrorCode(messageColon.substring(0, indexSpace));
                        codeMessage.setMessage(messageColon.substring(indexSpace+1, messageColon.length()));
                    }else{
                        codeMessage.setErrorCode(APIResultCode.EOS_ERROR.getCode());
                        codeMessage.setMessage(messageDetail);
                    }
                    return codeMessage;
                }else{
                    codeMessage.setErrorCode(APIResultCode.EOS_ERROR.getCode());
                    codeMessage.setMessage(messageDetail);
                }
            }
        }catch (Exception e){
            return codeMessage;
        }
        return codeMessage;
    }
}
