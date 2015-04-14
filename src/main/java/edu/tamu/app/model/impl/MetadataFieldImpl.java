/* 
 * DocumentImpl.java 
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.tamu.app.model.MetadataField;

/**
 * 
 * @author 
 *
 */
@Entity
@Table(name="all_metadata")
public class MetadataFieldImpl implements MetadataField {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="label")
	private String label;
		
	@Column(name="value")
	private String value;
	
	@Column(name="is_repeatable")
	private boolean isRepeatable;
	
	@Column(name="index")
	private int index;
	
	@Column(name="status")
	private String status;
	
	@ManyToOne
	private DocumentProfileImpl profile;
	
	/**
	 * Empty constructor.
	 */
	public MetadataFieldImpl() {
		super();
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String name, String label, String value, boolean isRepeatable) {
		super();
		this.name = name;
		this.label = label;
		this.value = value;
		this.isRepeatable = isRepeatable;
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String name, String label, String value, boolean isRepeatable, int index) {
		super();
		this.name = name;
		this.label = label;
		this.value = value;
		this.isRepeatable = isRepeatable;
		this.index = index;
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String name, String label, String value, boolean isRepeatable, int index, String status) {
		super();
		this.name = name;
		this.label = label;
		this.value = value;
		this.isRepeatable = isRepeatable;
		this.index = index;
		this.status = status;
	}

	/**
	 * 	Gets id.
	 * 
	 * @return		Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets id.
	 * 
	 * @param 		id				Long
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets name.
	 * 
	 * @return		String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 * 
	 * @param 		name			String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets label.
	 * 
	 * @return		String
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label.
	 * 
	 * @param 		label			String
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets value.
	 * 
	 * @return		String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets value.
	 * 
	 * @param 		value			String
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets isRepeatable.
	 * 
	 * @return		boolean
	 */
	public boolean getIsRepeatable() {
		return isRepeatable;
	}

	/**
	 * Sets isRepeatable.
	 * 
	 * @param 		isRepeatable	boolean
	 */
	public void setRepeatable(boolean isRepeatable) {
		this.isRepeatable = isRepeatable;
	}

	/**
	 * Gets index.
	 * 
	 * @return		int
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets index.
	 * 
	 * @param 		index			int
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Gets status.
	 * 
	 * @return		String
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets status.
	 * 
	 * @param 		status			String
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public DocumentProfileImpl getProfile() {
		return profile;
	}

	public void setProfile(DocumentProfileImpl profile) {
		this.profile = profile;
	}

}
