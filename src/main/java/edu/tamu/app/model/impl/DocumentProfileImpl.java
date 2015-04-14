package edu.tamu.app.model.impl;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;

import edu.tamu.app.model.DocumentProfile;

@Entity
@Table(name="all_profiles")
public class DocumentProfileImpl implements DocumentProfile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@OneToOne
	private DocumentImpl document;
	
	@OneToMany(fetch = FetchType.EAGER)
	private List<MetadataFieldImpl> metadataFields;
	
	public DocumentProfileImpl() {}
	
	public DocumentProfileImpl(String name) {
		this.name = name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {	
		return name;
	}

	@Override
	public void addMetadataFieldImpl(MetadataFieldImpl metadataFieldImpl) {
		metadataFields.add(metadataFieldImpl);
	}

	@Override
	public void removeMetadataFieldImpl(MetadataFieldImpl metadataFieldImpl) {
		metadataFields.remove(metadataFieldImpl);	
	}

	@Override
	public List<MetadataFieldImpl> getMetadataFields() {
		return metadataFields;
	}
	
	

}
