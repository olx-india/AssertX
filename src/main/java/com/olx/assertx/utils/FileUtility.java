package com.olx.assertx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class FileUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtility.class);
    private static final String FILE_PATH_FORMAT = "%s/%s";
    private static final String NEW_LINE_CHAR = "\n";

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    public static void copyDir(String srcPath, String destPath) throws IOException {
        LOGGER.debug("Copy directory with source path={} and destination path={}", srcPath, destPath);
        File srcDir = Paths.get(srcPath).toFile();
        File destDir = Paths.get(destPath).toFile();
        if (!srcDir.toString().equals(destDir.toString())) {
            FileUtils.copyDirectory(srcDir, destDir);
        }
    }

    public static void copyJarDir(String srcPath, String destPath, List<String> fileNames)
            throws IOException {
        LOGGER.debug("Copy jar directory with source path={}, destination path={} and files={}", srcPath, destPath, fileNames);
        File srcDir = new File(Objects.requireNonNull(FileUtility.class.getClassLoader().getResource(srcPath)).getFile());
        File destDir = Paths.get(destPath).toFile();
        if (!srcDir.toString().equals(destDir.toString())) {
            for (String fileName : fileNames) {
                copyFile(srcPath + "/" + fileName, destPath, fileName);
            }
        }
    }

    public static void copyFile(String templateFilePath, String destFilePath, String fileName) throws IOException {
        generateFileFromTemplate(templateFilePath, destFilePath, fileName, new HashMap<>());
    }

    public static void generateFileFromTemplate(String templateFilePath, String destFilePath, String fileName,
                                                Map<String, String> templateVariables) throws IOException {
        LOGGER.debug("Copy file from source path={} to destination path={} with template variables={}",
                templateFilePath, destFilePath, templateVariables);
        Files.createDirectories(Paths.get(destFilePath));
        try (FileWriter writer = new FileWriter(String.format(FILE_PATH_FORMAT, destFilePath, fileName))) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(FileUtility.class.getClassLoader().getResourceAsStream(templateFilePath))))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (String key : templateVariables.keySet()) {
                        line = line.replace(key, templateVariables.get(key));
                    }
                    writer.write(line + NEW_LINE_CHAR);
                }
            } catch (Exception e) {
                LOGGER.warn("Error writing file having destination path={}, exception={}",
                        destFilePath, e.getMessage());
            }
        }catch (Exception e){
            LOGGER.warn("Error generating file having destination path={}, exception={}",
                    destFilePath, e.getMessage());
        }
    }

    public static void deleteDirectory(String directoryPath) throws IOException {
        LOGGER.debug("Delete directory with path={}", directoryPath);
        File directory = new File(Paths.get(directoryPath).toString());
        FileUtils.deleteDirectory(directory);
    }

    public static void writeFile(String destFilePath, String fileName, Object object) throws IOException {
        LOGGER.debug("Write object of type={} to file={} at path={}", object.getClass(), fileName, destFilePath);
        Files.createDirectories(Paths.get(destFilePath));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.format(FILE_PATH_FORMAT, destFilePath, fileName)))) {
            writer.write(getObjectMapper().writeValueAsString(object));
        }
    }

    public static List<String> getFilteredFileList(String filesDirectoryPath, String[] fileExtension, boolean recursive) {
        Collection<File> filteredFiles = FileUtils.listFiles(new File(filesDirectoryPath), fileExtension, recursive);

        List<String> filePaths = new ArrayList<>(filteredFiles.size());
        filteredFiles.forEach(file -> filePaths.add(file.getAbsolutePath()));

        return filePaths;
    }

}
