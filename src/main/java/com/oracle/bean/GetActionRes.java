package com.oracle.bean;

import org.bson.Document;

import java.util.List;

public class GetActionRes {
    private List<DocumentWithBlock> actions;
    private Integer page;
    private Integer pageSize;
    private Boolean hasMore;

    public List<DocumentWithBlock> getActions() {
        return actions;
    }

    public void setActions(List<DocumentWithBlock> actions) {
        this.actions = actions;
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

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}
