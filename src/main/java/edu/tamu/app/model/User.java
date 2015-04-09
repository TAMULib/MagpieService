/* 
 * User.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

/**
 * User interface. lol
 * 
 * @author
 *
 */
public interface User {

	/**
	 * Sets UIN.
	 * 
	 * @param 		uin				Long
	 * 
	 */
	public void setUin(Long uin);
	
	/**
	 * Gets UIN.
	 * 
	 * @return		Long
	 * 
	 */
	public Long getUin();
	
	/**
	 * Gets firstName.
	 * 
	 * @return		String
	 */
	public String getFirstName();

	/**
	 * Sets firstName.
	 * 
	 * @param 		firstName		String
	 */
	public void setFirstName(String firstName);

	/**
	 * Gets lastName.
	 * 
	 * @return		String
	 */
	public String getLastName();

	/**
	 * Sets lastName.
	 * 
	 * @param 		lastName		String
	 */
	public void setLastName(String lastName);
	
	/**
	 * Sets role.
	 * 
	 * @param 		role			String
	 * 
	 */
	public void setRole(String role);
	
	/**
	 * Gets role.
	 * 
	 * @return		String
	 * 
	 */
	public String getRole();


}

