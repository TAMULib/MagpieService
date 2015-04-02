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
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="label")
	private String label;
		
	@Column(name="value")
	private String value;
	
	@Column(name="is_repeatable")
	private boolean isRepeatable;
	
	@Column(name="index")
	private int index;
	
	/**
	 * 
	 */
	public MetadataFieldImpl() {
		super();
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String filename) {
		super();
		this.filename = filename;
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String filename, String label, String value, boolean isRepeatable) {
		super();
		this.filename = filename;
		this.label = label;
		this.value = value;
		this.isRepeatable = isRepeatable;
	}
	
	/**
	 * 
	 */
	public MetadataFieldImpl(String filename, String label, String value, boolean isRepeatable, int index) {
		super();
		this.filename = filename;
		this.label = label;
		this.value = value;
		this.isRepeatable = isRepeatable;
		this.index = index;
	}

	/**
	 * 	
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRepeatable() {
		return isRepeatable;
	}

	/**
	 * 
	 * @param isRepeatable
	 */
	public void setRepeatable(boolean isRepeatable) {
		this.isRepeatable = isRepeatable;
	}

	/**
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
