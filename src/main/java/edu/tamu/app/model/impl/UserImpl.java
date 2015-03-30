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
	
	@Override
	public void setUin(Long uin) {
		this.uin = uin;
	}
	
	@Override
	public Long getUin() {
		return uin;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String getRole() {
		return role;
	}
}
