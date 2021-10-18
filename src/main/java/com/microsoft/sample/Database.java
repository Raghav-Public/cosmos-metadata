package com.microsoft.sample;

public class Database {
	String name;
	String rid;
	
	public String getName() {
		return this.name;
	}
	public String getRid() {
		return this.rid;
	}
	
	public Database(String n, String rid) {
		this.name = n;
		this.rid = rid;
	}
 }
