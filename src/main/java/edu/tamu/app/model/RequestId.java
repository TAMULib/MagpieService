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
	 * Constructs new RequestId.
	 * 
	 * @param 		id			String
	 */
	public RequestId(String id) {
		this.id = id;
	}

	/**
	 * Gets id.
	 * 
	 * @return		String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets id.
	 * 
	 * @param 		id			String
	 */
	public void setId(String id) {
		this.id = id;
	}
	
}
