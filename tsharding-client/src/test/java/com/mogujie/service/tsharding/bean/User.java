package com.mogujie.service.tsharding.bean;

public class User {
	private long id;
	private String name;

	public long getId() {
		return id;
	}

	public User() {
	}

	public User(long id) {
		this.id = id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return id + "-" + name;
	}
}
