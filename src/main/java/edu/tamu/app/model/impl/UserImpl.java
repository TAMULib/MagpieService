/* 
 * UserImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.app.model.User;

/**
 * Implementation of user object.
 * 
 * @author 
 *
 */
@Entity
@Table(name="all_users")
public class UserImpl implements User {
	
	@Id
	@Column(name="uin", nullable=false)
	private Long uin;
	
	@Column(name="firstname")
	private String firstName;
	
	@Column(name="lastname")
	private String lastName;
	
	@Column(name="role")
	private String role;
	
	/**
	 * Default constructor.
	 * 
	 */
	public UserImpl() {
		super();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param 		uin				Long
	 * 
	 */
	public UserImpl(Long uin) {
		super();
		this.uin = uin;
	}
	
	/**
	 * Sets UIN.
	 * 
	 * @param 		uin				Long
	 * 
	 */
	@Override
	public void setUin(Long uin) {
		this.uin = uin;
	}
	
	/**
	 * Gets UIN.
	 * 
	 * @return		Long
	 * 
	 */
	@Override
	public Long getUin() {
		return uin;
	}
	
	/**
	 * Gets firstName.
	 * 
	 * @return		String
	 * 
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets firstName.
	 * 
	 * @param 		firstName		String
	 * 
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets lastName.
	 * 
	 * @return		String
	 * 
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets lastName.
	 * 
	 * @param 		lastName		String
	 * 
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets role.
	 * 
	 * @param 		role			String
	 * 
	 */
	@Override
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Gets role.
	 * 
	 * @return		String
	 * 
	 */
	@Override
	public String getRole() {
		return role;
	}
	
}
