package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanOrders implements Serializable {
    public List<BeanOrdersDetail> items;
    public int pages;
}
