/**
  * Copyright 2018 bejson.com 
  */
package com.oracle.EOS.bean;

/**
 * Auto-generated: 2018-06-27 11:9:47
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class EosErrorRes {

    private int code;
    private String message;
    private Error error;
    public void setCode(int code) {
         this.code = code;
     }
     public int getCode() {
         return code;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setError(Error error) {
         this.error = error;
     }
     public Error getError() {
         return error;
     }

}