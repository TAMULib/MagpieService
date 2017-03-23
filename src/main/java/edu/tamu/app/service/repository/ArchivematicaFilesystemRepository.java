package edu.tamu.app.service.repository;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import edu.tamu.app.model.Document;

public class ArchivematicaFilesystemRepository implements Repository {
    
    private String archivematicaDirectoryName = "/Users/jcreel/Development/MetadataAssignmentToolService/src/main/resources/static/metadatatool/archivematica";

    @Override
    public Document push(Document document) throws IOException {
        System.out.println("Writing Archivematica Transfer Package for Document " + document.getName() + " to " + archivematicaDirectoryName);
        
        //assume for now that there are some number of tiffs in the document directory
        File documentDirectory = new File(document.getDocumentPath());
        
        File[] tiffFiles = documentDirectory.listFiles(new OnlyTiff());
        
        
        
        
        
        
        return document;
    }
    
    class OnlyTiff implements FilenameFilter {
       
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".tif") || name.toLowerCase().endsWith(".tiff");
        }
    }

}
