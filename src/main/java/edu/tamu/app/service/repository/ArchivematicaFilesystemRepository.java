package edu.tamu.app.service.repository;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import edu.tamu.app.model.Document;
import edu.tamu.app.utilities.CsvUtility;
import edu.tamu.app.utilities.FileSystemUtility;

public class ArchivematicaFilesystemRepository implements Repository {
    
    @Value("${app.mount}")
    private String mount;

    @Autowired
    private CsvUtility csvUtility;
    
    private String archivematicaDirectoryName;

    @Autowired
    private ResourceLoader resourceLoader;

    public ArchivematicaFilesystemRepository(String archivematicaDirectoryName) {
        this.archivematicaDirectoryName = archivematicaDirectoryName;
    }

    @Override
    public Document push(Document document) throws IOException {

        Path itemDirectoryName = FileSystemUtility.getWindowsSafePath(
                resourceLoader.getResource("classpath:static").getURL().getPath() + document.getDocumentPath());
        System.out.println("Writing Archivematica Transfer Package for Document " + itemDirectoryName.toString()
                + " to " + archivematicaDirectoryName);

        File documentDirectory = itemDirectoryName.toFile();

        // assume for now that there are some number of tiffs in the document
        // directory
        // obtain the tiffs and md5 checksum from disk
        File[] tiffFiles = documentDirectory.listFiles(new OnlyTiff());
        for (File tiff : tiffFiles) {
            System.out.println("Found tiff " + tiff.getName());
        }

        File[] md5hash = documentDirectory.listFiles(new OnlyMD5());
        for (File md5 : md5hash) {
            System.out.println("Found md5 " + md5.getName());
        }

        if (md5hash.length < 1) {
            throw new IOException("md5hash missing for Archivematica document " + document.getName());
        }

        // make the top level container for the transfer package
        File transferPackageDirectory = new File(archivematicaDirectoryName + "/" + document.getName());
        if (!transferPackageDirectory.isDirectory())
            transferPackageDirectory.mkdir();

        // make the logs, metadata, and objects subdirectories
        File logsSubdirectory = new File(transferPackageDirectory.getPath() + "/logs");
        if (!logsSubdirectory.isDirectory())
            logsSubdirectory.mkdir();

        File metadataSubdirectory = new File(transferPackageDirectory.getPath() + "/metadata");
        if (!metadataSubdirectory.isDirectory())
            metadataSubdirectory.mkdir();

        File objectsSubdirectory = new File(transferPackageDirectory.getPath() + "/objects");
        if (!objectsSubdirectory.isDirectory())
            objectsSubdirectory.mkdir();

        // copy the checksum to the metadata subdirectory
        File md5Destination = new File(metadataSubdirectory.getPath() + "/checksum.md5");
        FileUtils.copyFile(md5hash[0], md5Destination);

        // write the metadata csv in the metadata subdirectory
        // write the ArchiveMatica CSV for this document
        csvUtility.generateOneArchiveMaticaCSV(document, metadataSubdirectory.getPath());

        // make the submissionDocumentation subdirectory in the metadata
        // subdirectory
        File submissionDocumentationSubdirectory = new File(
                metadataSubdirectory.getPath() + "/submissionDocumentation");
        if (!submissionDocumentationSubdirectory.isDirectory())
            submissionDocumentationSubdirectory.mkdir();

        // copy the item directory and tiffs to the objects subdirectory
        for (File tiff : tiffFiles) {
            File destinationTiff = new File(objectsSubdirectory.getPath() + "/" + tiff.getName());
            FileUtils.copyFile(tiff, destinationTiff);
        }

        return document;
    }

    class OnlyTiff implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".tif") || name.toLowerCase().endsWith(".tiff");
        }
    }

    class OnlyMD5 implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".md5");
        }
    }

}
