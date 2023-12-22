package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanComplaint implements Serializable {
    public int total;
    public int pages;
    public int pageNum;
    public List<Items> items;

    public class Items implements Serializable{
        public String appealTime;
        public String shipperId;
        public String appealContent;
        public String complaintContent;
        public String addressEnd;
        public int resultsState;
        public String resultsTime;
        public String ordersDispatchId;
        public String ctime;
        public String addressStart;
        public int id;
        public String carrierId;
        public String resultsContent;
        public int status;
        public String shipperName;
    }
}
