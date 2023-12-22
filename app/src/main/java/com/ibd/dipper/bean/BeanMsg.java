package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanMsg implements Serializable {
    public String noticeContent;
    public int complaintUnread;
    public int evaluateUnread;
    public String evaluateTime;
    public int unread;
    public String evaluateContent;
    public int noticeUnread;
    public String complaintContent;
    public String complaintTime;
    public String noticeTime;

    public int pages;
    public List<Items> items;

    public class Items {
        public int read;
        public OrderInfo orderInfo;
        public String ctime;

        public int standard;
        public int safety;
        public int service;
        public String title;
        public int speed;
        public int attitude;

        public String appealTime;
        public String complaintContent;
        public String appealContent;
        public String complaintTime;
        public int id;
        public String resultsTime;
        public String resultsContent;
        public int state;
    }

    public class OrderInfo {

        public String shiperName;
        public String dispatchQuantity;
        public String carriersFreight;
        public String deliveryAddress;
        public String receivingAddress;
        public String transporMileage;
        public String goods;
        public String ordersDispatcheId;


    }
}
