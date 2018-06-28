package com.oracle.bean;


import com.google.gson.annotations.Expose;

public class EosNodeTableRequest {
    @Expose
    private boolean json = true;

    @Expose
    private String scope;

    @Expose
    private String code;

    @Expose
    private String table;

    @Expose
    private Long lower_bound;

    @Expose
    private Long upper_bound;

    @Expose
    private Long limit;

    public EosNodeTableRequest(String scope, String code, String table ) {
        this.scope = scope;
        this.code = code;
        this.table = table;
        lower_bound = 0l;
        upper_bound = -1l;
    }


    public EosNodeTableRequest(String scope, String code, String table, Long lower_boundPar, Long upper_boundPar, Long limitPar) {
        this.scope = scope;
        this.code = code;
        this.table = table;
        lower_bound = lower_boundPar;
        upper_bound = upper_boundPar;
        limit = limitPar;
    }
}
