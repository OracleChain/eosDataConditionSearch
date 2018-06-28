package com.oracle.bean;

import java.util.List;

public class GetActionsReq {
    private List<ContractSymbol> symbols;
    private String from;
    private String to;
    private Integer page;
    private Integer pageSize;

    public List<ContractSymbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<ContractSymbol> symbols) {
        this.symbols = symbols;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
