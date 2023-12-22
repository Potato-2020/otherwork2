package com.ibd.dipper.service;

public class Data{
    private static String orderID ="feiyangxiaomi";

    public static String getOrderID() {
        return orderID;
    }

    public static void setOrderID(String orderID) {
        Data.orderID = orderID;
    }
}
