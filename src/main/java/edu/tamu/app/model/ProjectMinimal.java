/* 
 * Project.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;


/**
 * 
 * 
 * @author 
 *
 */

public class ProjectMinimal {
	private String name;
	private Boolean isLocked;
	
	public ProjectMinimal(String name,Boolean isLocked) {
		setName(name);
		setIsLocked(isLocked);
	}

	public Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(Boolean status) {
		this.isLocked = status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
