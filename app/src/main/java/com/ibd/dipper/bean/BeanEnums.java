package com.ibd.dipper.bean;

import java.util.List;

public class BeanEnums {
    public String name;
    public int id;

    public List<Items> items;

    public class Items {
        public String label;
        public String value;
        public List<Items> children;
    }
}
