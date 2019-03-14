package com.bupt.common.bean;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


public class Pager {
    private Integer pager; // 当前页码
    private Integer pagerSize; // 每页记录数
    private Long total; // 总记录数

    public Pager(Integer pager, Integer pagerSize, Long total) {
        this.pager = pager;
        this.pagerSize = pagerSize;
        this.total = total;
    }

    public Integer getPager() {
        return pager;
    }

    public void setPager(Integer pager) {
        this.pager = pager;
    }

    public Integer getPagerSize() {
        return pagerSize;
    }

    public void setPagerSize(Integer pagerSize) {
        this.pagerSize = pagerSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
