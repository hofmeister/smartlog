package com.vonhof.smartlog.dto;

import java.util.LinkedList;
import java.util.List;

public class PagedList<T> {
    private final List<T> rows = new LinkedList<T>();
    private long offset;
    private long total;

    public List<T> getRows() {
        return rows;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
