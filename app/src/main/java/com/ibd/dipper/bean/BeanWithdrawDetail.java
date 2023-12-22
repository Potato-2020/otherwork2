package com.ibd.dipper.bean;

import java.io.Serializable;
import java.util.List;

public class BeanWithdrawDetail implements Serializable {
    public int pages;
    public List<Items> items;

    public class Items implements Serializable{
        public String otherAcc;
        public String postBala;
        public String transTime;
        public int chgFlag;
        public String transDate;
        public String businName;
        public String amt;
        public String otherAccName;
        public String BnakName;
        public String BnakAccount;
    }
}
