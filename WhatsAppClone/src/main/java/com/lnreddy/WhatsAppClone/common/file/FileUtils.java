package com.lnreddy.WhatsAppClone.common.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class FileUtils {

    private FileUtils(){}


    public static byte[] readFileFromLocation(String filePath){
        if (filePath == null || filePath.isBlank()) {
            log.warn("Empty or null file path provided");
            return new byte[0];
        }
        try {
            Path file= new File(filePath).toPath();
            return Files.readAllBytes(file);
        } catch (IOException e) {
            log.warn("Failed to read file at path {}. Error: {}", filePath, e.getMessage());
        }
        return new byte[0];
    }

}
