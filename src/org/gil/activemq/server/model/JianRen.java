package org.gil.activemq.server.model;

import java.io.Serializable;

public class JianRen implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	String info;
	public JianRen(String name,String info) {
		this.info=info;
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
