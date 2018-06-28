/**
  * Copyright 2018 bejson.com 
  */
package com.oracle.EOS.bean;
import java.util.List;

/**
 * Auto-generated: 2018-06-27 11:9:47
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Error {

    private long code;
    private String name;
    private String what;
    private List<Details> details;
    public void setCode(long code) {
         this.code = code;
     }
     public long getCode() {
         return code;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setWhat(String what) {
         this.what = what;
     }
     public String getWhat() {
         return what;
     }

    public void setDetails(List<Details> details) {
         this.details = details;
     }
     public List<Details> getDetails() {
         return details;
     }

}