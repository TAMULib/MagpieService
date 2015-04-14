package edu.tamu.app.model;

import java.util.List;

import edu.tamu.app.model.impl.MetadataFieldImpl;

public interface DocumentProfile {
	
	public void setName(String name);
	public String getName();
	
	public void addMetadataFieldImpl(MetadataFieldImpl metadataFieldImpl);
	public void removeMetadataFieldImpl(MetadataFieldImpl metadataFieldImpl);

	public List<MetadataFieldImpl> getMetadataFields();

}
