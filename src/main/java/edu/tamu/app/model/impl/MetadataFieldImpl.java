/* 
 * MetadataFieldImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.impl;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.app.model.MetadataField;

/**
 * Implementation of metadatafield object.
 * 
 * @author 
 *
 */
@Entity
@Table(name="all_metadata")
public class MetadataFieldImpl implements MetadataField {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private String label;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> values;
			
	/**
	 * Default constructor.
	 * 
	 */
	public MetadataFieldImpl() {
		super();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param 		name 			String
	 * @param		label			String
	 * @param		values			List<String>
	 * 
	 */
	public MetadataFieldImpl(String name, String label, List<String> values) {
		super();
		this.name = name;
		this.label = label;
		this.values = values;
	}

	/**
	 * Gets id.
	 * 
	 * @return		Long
	 * 
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets id.
	 * 
	 * @param 		id				Long
	 * 
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets name.
	 * 
	 * @return		String
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 * 
	 * @param 		name			String
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets label.
	 * 
	 * @return 		String
	 * 
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label.
	 * 
	 * @param 		label			String
	 * 
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets value.
	 * 
	 * @return		String
	 * 
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * Sets value.
	 * 
	 * @param 		value			String
	 * 
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

}
