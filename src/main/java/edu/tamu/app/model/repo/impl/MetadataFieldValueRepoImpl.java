/* 
 * MetadataFieldValueRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.custom.MetadataFieldValueRepoCustom;

/**
 *
 * 
 * @author
 *
 */
public class MetadataFieldValueRepoImpl implements MetadataFieldValueRepoCustom {

	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;

	@Override
	public synchronized MetadataFieldValue create(ControlledVocabulary cv, MetadataFieldGroup field) {
		MetadataFieldValue metadataFieldValue = metadataFieldValueRepo.findByCvAndField(cv, field);
        if (metadataFieldValue == null) {
            metadataFieldValue = metadataFieldValueRepo.save(new MetadataFieldValue(cv, field));
        }
        return metadataFieldValue;
	}

	@Override
	public synchronized MetadataFieldValue create(String value, MetadataFieldGroup field) {
		MetadataFieldValue metadataFieldValue = metadataFieldValueRepo.findByValueAndField(value, field);
        if (metadataFieldValue == null) {
            metadataFieldValue = metadataFieldValueRepo.save(new MetadataFieldValue(value, field));
        }
        return metadataFieldValue;
	}

	@Override
	public synchronized MetadataFieldValue create(String value, MetadataFieldGroup field, ControlledVocabulary cv) {
		MetadataFieldValue metadataFieldValue = metadataFieldValueRepo.findByValueAndCvAndField(value, cv, field);
        if (metadataFieldValue == null) {
            metadataFieldValue = metadataFieldValueRepo.save(new MetadataFieldValue(value, cv, field));
        }
        return metadataFieldValue;
	}

}
