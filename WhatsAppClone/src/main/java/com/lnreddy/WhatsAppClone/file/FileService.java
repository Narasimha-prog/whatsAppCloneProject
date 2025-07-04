package com.lnreddy.WhatsAppClone.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${application.file.uploads.media-output-path}")
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

        File targetFolder=new File(fileUploadSubPath);

        if(!targetFolder.exists()){
          boolean folderCreated= targetFolder.mkdir();
          if(!folderCreated){
              log.warn("Failed to Create Target folder ,{}",targetFolder);
              return  null;
          }
        }
        final String fileExtension=getFileExtention(sourceFile.getOriginalFilename());

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
