package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanBank implements Serializable {
    public List<Items> items;

    public class Items implements Serializable{
        public String bindFlagName;
        public String bankNo;
        public String bankName;
        public String bankAcc;
        public boolean choosed;
    }
}
