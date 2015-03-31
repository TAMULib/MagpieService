/* 
 * RequestId.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

public class RequestId {
	
	private String id;
	
	/**
	 * 
	 * @param id
	 */
	public RequestId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
}
