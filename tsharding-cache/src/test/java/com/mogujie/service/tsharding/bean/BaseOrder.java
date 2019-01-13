package com.mogujie.service.tsharding.bean;

import java.io.Serializable;

public abstract class BaseOrder implements Serializable {
	private static final long serialVersionUID = 8641204480746103195L;
	private int id;
	private Long orderId;

	private Long buyerUserId;

	private Long sellerUserId;

	private Long shipTime;

	public Long getShipTime() {
		return shipTime;
	}

	public void setShipTime(Long shipTime) {
		this.shipTime = shipTime;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(Long buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public Long getSellerUserId() {
		return sellerUserId;
	}

	public void setSellerUserId(Long sellerUserId) {
		this.sellerUserId = sellerUserId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
