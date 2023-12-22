package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanOnline implements Serializable {
    public int total;
    public int pages;
    public int pageNum;
    public List<Items> items;

    public class Items implements Serializable {
        public int replied;
        public String repliedTime;
        public String ctime;
        public int id;
        public String reply;
        public List<String> photos;
        public String content;
    }
}
