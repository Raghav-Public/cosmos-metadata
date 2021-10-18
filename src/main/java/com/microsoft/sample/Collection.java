package com.microsoft.sample;

public class Collection {
	private String name;
	private String rid;
	
	public String getName() {
		return this.name;
	}
	public String getRid() {
		return this.rid;
	}
	
	public Collection(String n, String rid) {
		this.name = n;
		this.rid = rid;
	}
}
