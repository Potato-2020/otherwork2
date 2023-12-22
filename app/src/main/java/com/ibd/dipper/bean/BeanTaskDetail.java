package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanTaskDetail implements Serializable {
    public String dispatchQuantity;
    public String unit;
    public List<Nodes> nodes;
    public String unitFreight;
    public String carriersFreight;
    public String deliveryAddress;
    public String deliveryAddressDetail;
    public String receivingAddress;
    public String receivingAddressDetail;
    public String goods;
    public String id;
    public String tmsOrderId;
    public String allowableLoss;
    public String deliveryCompany;
    public String receivingCompany;

    public String ctime;
    public int checkStatus;
    public String checkStatusText;
    public String reason;

    public class Nodes implements Serializable{
        public int node;
        public String ctime;
        public String unit;
        public String quantity;
        public String receiptNo;
        public List<String> photos;
    }
}
