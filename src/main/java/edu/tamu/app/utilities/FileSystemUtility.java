package edu.tamu.app.utilities;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileSystemUtility {

    /**
     * Retrieves a list of directories in a directory.
     * 
     * @param directory
     *            String
     * 
     * @return List<Path>
     * 
     */
    public static List<Path> directoryList(String directory) {
        List<Path> fileNames = new ArrayList<>();

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            public boolean accept(Path path) throws IOException {
                return (Files.isDirectory(path));
            }
        };

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory), filter)) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {
        }
        return fileNames;
    }

    /**
     * Retrieves a list of files in a directory.
     * 
     * @param directory
     *            String
     * 
     * @return List<Path>
     * 
     */
    public static List<Path> fileList(String directory) {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {
        }
        return fileNames;
    }
    
    public static void createDirectory(String path) throws IOException {
        Path newDirectoryPath = Paths.get(path);
        if (!Files.exists(newDirectoryPath)) {
            Files.createDirectory(newDirectoryPath);
        }
    }
    
    public static void createFile(String path, String name) throws IOException {
        Path newFilePath = Paths.get(path, name);
        if (!Files.exists(newFilePath)) {
            Files.createFile(newFilePath);
        }
    }

}
