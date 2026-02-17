package com.lnreddy.WhatsAppClone.common.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${spring.application.file.uploads.media-output-path}")
    private String fileUploadPath;

    public String saveFile(
     @NonNull MultipartFile sourceFile,
      @NonNull      String userId)
    {
        final String fileUploadSubPath="users"+ File.separator+userId;
        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile(
       @NonNull     MultipartFile sourceFile,
        @NonNull    String fileUploadSubPath) {
        final String finalUploadPath=fileUploadPath+File.separator+fileUploadSubPath;

        File targetFolder=new File(finalUploadPath);

        if(!targetFolder.exists()){
          boolean folderCreated= targetFolder.mkdirs();
          if(!folderCreated){
              log.warn("Failed to Create Target folder ,{}",targetFolder);
              return  null;
          }
        }
        final String fileExtension=getFileExtention(sourceFile.getOriginalFilename());

        String targetFilePath=finalUploadPath+File.separator+System.currentTimeMillis()+"."+fileExtension;
        log.info(targetFilePath);
        Path targetPath= Paths.get(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved at TargetPath {} ",targetPath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not saved... ",e);
        }
        return finalUploadPath;
    }

    private String getFileExtention(String originalFilename) {
        if(originalFilename==null || originalFilename.isEmpty()){
            return "";
        }
        int lastDotIndex=originalFilename.lastIndexOf(".");
        if(lastDotIndex==-1){
            return "";
        }

        return originalFilename.substring(lastDotIndex+1).toLowerCase();
    }
}
