package com.bupt.service.bean.bill;

import java.math.BigDecimal;
import java.util.Date;

public class Bill {
    private Long id;

    private String consumeType;

    private BigDecimal money;

    private String consumeInfo;

    private Date consumeCreate;

    private Date consumeUpdate;

    private Integer year;

    private Integer month;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType == null ? null : consumeType.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getConsumeInfo() {
        return consumeInfo;
    }

    public void setConsumeInfo(String consumeInfo) {
        this.consumeInfo = consumeInfo == null ? null : consumeInfo.trim();
    }

    public Date getConsumeCreate() {
        return consumeCreate;
    }

    public void setConsumeCreate(Date consumeCreate) {
        this.consumeCreate = consumeCreate;
    }

    public Date getConsumeUpdate() {
        return consumeUpdate;
    }

    public void setConsumeUpdate(Date consumeUpdate) {
        this.consumeUpdate = consumeUpdate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}