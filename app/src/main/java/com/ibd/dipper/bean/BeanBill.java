package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanBill implements Serializable {

    public int total;
    public int pages;
    public int pageNum;
    public int noPaymentCout;
    public String noPaymentAmtTotal;
    public List<Items> items;

    public class Items implements Serializable {
        public String dispatchQuantity;
        public String carriersFreight;
        public String payable;
        public String ordersDispatchId;
        public String transporMileage;
        public String goods;
        public String noPaymentAmount;
        public String deliveryCompany;
        public String paymentAmount;
        public String receivingCompany;
        public String deliveryAddress;
        public String receivingAddress;
        public int paymentMethod;
        public String paymentMethodText;
        public String id;
        public String ctime;
    }
}
