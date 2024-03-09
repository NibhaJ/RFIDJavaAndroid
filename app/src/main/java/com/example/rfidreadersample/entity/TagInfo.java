package com.example.rfidreadersample.entity;

import java.io.Serializable;
import java.util.Date;

public class TagInfo implements Serializable {
    private Long index;
    private String type;
    private String epc;
    private Long count;
    private String tid;
    private String rssi;
    private String userData;
    private String reservedData;
    private Date readTime;

    public TagInfo() {
    }

    public TagInfo(Long index, String type, String epc, String tid, String rssi) {
        this.index = index;
        this.type = type;
        this.epc = epc;
        this.tid = tid;
        this.rssi = rssi;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getReservedData() {
        return reservedData;
    }

    public void setReservedData(String reservedData) {
        this.reservedData = reservedData;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return "TagInfo{" +
                "index=" + index +
                ", type='" + type + '\'' +
                ", epc='" + epc + '\'' +
                ", count=" + count +
                ", tid='" + tid + '\'' +
                ", rssi='" + rssi + '\'' +
                ", userData='" + userData + '\'' +
                ", reservedData='" + reservedData + '\'' +
                '}';
    }
}
