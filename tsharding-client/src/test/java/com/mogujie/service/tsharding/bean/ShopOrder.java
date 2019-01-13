package com.mogujie.service.tsharding.bean;

public class ShopOrder extends BaseOrder {
    @Override
    public String toString() {
        return "{id:" + super.getId() + ",orderId:" + super.getOrderId() + ",buyerUserId:" + super.getBuyerUserId()
                + ",sellerUserId:" + super.getSellerUserId() + ",shipTime:" + super.getShipTime() + "}";
    }
}
