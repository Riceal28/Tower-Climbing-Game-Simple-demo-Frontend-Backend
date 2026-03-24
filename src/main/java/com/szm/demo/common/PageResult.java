package com.szm.demo.common;

import java.util.List;

public class PageResult<T> {

    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private List<T> list;
    private Boolean hasNext;
    private Boolean hasPrev;

    public static <T> PageResult<T> of(Long total,Integer page,Integer size,List<T> list){
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setTotalPage((int) Math.ceil((double) total / size));
        pageResult.setList(list);
        pageResult.setHasNext(page < pageResult.getTotalPage());
        pageResult.setHasPrev(page>1);
        return pageResult;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(Boolean hasPrev) {
        this.hasPrev = hasPrev;
    }
}
