package com.mogujie.service.tsharding.bean;

import java.io.Serializable;

public class Product implements Serializable {
	private static final long serialVersionUID = -3096898537387257249L;
	private int id;
	private String name;
	private Double price;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "{id:" + id + ",name:" + name + ",price:" + price + "}";
	}
}
