package edu.tamu.app.comparator;

import java.util.Comparator;

import edu.tamu.app.model.MetadataFieldGroup;

/**
 * Class for comparing MetadataFieldGroup by name.
 * 
 * @author
 *
 */
public class LabelComparator implements Comparator<MetadataFieldGroup> {

    /**
     * Compare name of MetadataFieldGroup
     * 
     * @param mfg1
     *            MetadataFieldGroup
     * @param mfg2
     *            MetadataFieldGroup
     * 
     * @return int
     */
    @Override
    public int compare(MetadataFieldGroup mfg1, MetadataFieldGroup mfg2) {
        return mfg1.getLabel().getName().compareTo(mfg2.getLabel().getName());
    }

}
