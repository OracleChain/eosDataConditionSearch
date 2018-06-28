package com.oracle.common;


import java.util.HashMap;
import java.util.Map;

public enum APIResultCode {

    SUCCESS("0", "成功"),
    PARAMETER_ERROR("1", "参数错误"),
    TRANSACTION_NOT_EXIST("2", "transaction id 不存在"),
    BALANCE_NOT_ENOUGH("3", "余额不足"),
    SIGNATURE_NOT_RIGHT("4", "签名不对"),
    UN_KNOW("5", "未知错误"),
    ACCOUNT_NOT_EXIST("6", "账户不存在"),
    NET_ERROR_TRY_LATER_AGAIN("7", "网络异常，稍后再试"),
    EOS_ERROR("8", "失败，见错误于EOS错误详情"),
    WALLET_NAME_EXISTS("10001", "WALLET_NAME_EXISTS"),
    WALLET_NAME_NOT_EXISTS("10002", "WALLET_NAME_NOT_EXISTS"),
    NO_DATA("10003", "NO_DATA"),
    SIGNATURE_NOT_VALIDATE("1004", "无效签名"),
    SIGNATURE_OUTDATE("1005", "签名过期");

    private static final Map<String, APIResultCode> interToEnum = new HashMap<String, APIResultCode>();

    static {
        for (APIResultCode type : APIResultCode.values()) {
            interToEnum.put(type.getCode(), type);
        }
    }

    private String result;
    private String message;

    private APIResultCode(String code, String message) {
        this.result = code;
        this.message = message;
    }

    public static APIResultCode fromCode(String code) {
        return interToEnum.get(code);
    }

    public String getCode() {
        return result;
    }

    public void setCode(String code) {
        this.result = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
