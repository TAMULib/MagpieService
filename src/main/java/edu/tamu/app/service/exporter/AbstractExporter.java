package edu.tamu.app.service.exporter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.ProjectsService;

public abstract class AbstractExporter implements Exporter {
	
	@Autowired
	protected ProjectRepo projectRepo;
	
	@Autowired
	protected ProjectsService projectsService;
	
	public static Predicate<Document> isPublished() {
        return d -> d.getStatus().equals("Published");
    }

    public static Predicate<Document> isAccepted() {
        return d -> d.getStatus().equals("Accepted");
    }

    public static Predicate<Document> isPending() {
        return d -> d.getStatus().equals("Pending");
    }
    
    /**
     * Class for comparing MetadataFieldImpl by label.
     * 
     * @author
     *
     */
    class LabelComparator implements Comparator<MetadataFieldGroup> {
        /**
         * Compare labels of MetadataFieldImpl
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
    
    protected List<String> performMetadataFieldsExtraction(Project project) {
		
		List<String> metadataHeaders = new ArrayList<String>();
		System.out.println("Extracting headers.");
    	projectsService.getProjectFields(project.getName()).forEach(mfg -> {
    		System.out.println("EXTRACTED: " + mfg.getLabel().getName());
    		metadataHeaders.add(mfg.getLabel().getName());
    	});
    	
    	System.out.println("RESULTING LIST: " + metadataHeaders);
    	
		return metadataHeaders;
	}
    
}
