package edu.tamu.app.utilities;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(getWindowsSafePath(directory), filter)) {
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
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(getWindowsSafePath(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {
        }
        return fileNames;
    }

    public static List<String> fileListAsStrings(String directory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(getWindowsSafePath(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        } catch (IOException ex) {
        }
        return fileNames;
    }

    public static void createDirectory(String path) throws IOException {
        Path newDirectoryPath = getWindowsSafePath(path);
        if (!Files.exists(newDirectoryPath)) {
            Files.createDirectory(newDirectoryPath);
        }
    }

    public static void createFile(String path, String name) throws IOException {
        Path newFilePath = getWindowsSafePath(path, name);
        if (!Files.exists(newFilePath)) {
            Files.createFile(newFilePath);
        }
    }

    public static void deleteDirectory(String path) throws IOException {
        Path directory = getWindowsSafePath(path);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public static Path getWindowsSafePath(String path) {
        return Paths.get(getWindowsSafePathString(path));
    }

    public static Path getWindowsSafePath(String path, String name) {
        return Paths.get(getWindowsSafePathString(path), name);
    }

    public static String getWindowsSafePathString(String path) {
        // note that a Windows path will contain one and only one colon character
        if (path.contains(":") && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }
        return path;
    }

}
