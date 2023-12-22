package com.ibd.dipper.ui.ordersDetail;

public class RefuseSuccessEvent {
    public boolean isOrders;
    public String id;
    public int status;
    public int biddingStatus;

    public RefuseSuccessEvent(boolean isOrders, String id, int status, int biddingStatus) {
        this.isOrders = isOrders;
        this.id = id;
        this.status = status;
        this.biddingStatus = biddingStatus;
    }
}
