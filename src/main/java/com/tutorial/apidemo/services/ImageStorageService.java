package com.tutorial.apidemo.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ImageStorageService implements IStorageService{
    private final Path storageFolder = Paths.get("uploads");
    public ImageStorageService(){
        try {
            Files.createDirectories(storageFolder);
        }catch (Exception exception) {
            throw new RuntimeException("can not initialize storage",exception);
        }
    }

    private boolean isImageFile(MultipartFile file){
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(new String[] {"png", "jpg", "jpeg","bmp"})
                .contains(fileExtension.trim().toLowerCase());
    }

    @Override
    public String storageFile(MultipartFile file) {
        try {
            // check file rong
            if(file.isEmpty()){
                throw new RuntimeException("Failed to store empty file");
            }
            // check file anh
            if(!isImageFile(file)){
                throw new RuntimeException("You can only uploads image files");
            }
            // check file size
            float fileSizeInMb = file.getSize() / 1024000.0f;
            if(fileSizeInMb > 5.0f){
                throw new RuntimeException("File must be < 5mb");
            }
            // rename file
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName = UUID.randomUUID()
                    .toString()
                    .trim()
                    .replace("-","");
            generatedFileName = generatedFileName + "." + fileExtension;
            Path destinationFilePath = this.storageFolder.resolve(
                    Paths.get(generatedFileName)
            ).normalize().toAbsolutePath();
//            if(destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())){
//                throw new RuntimeException(
//                        "Cannot store file outside current directory"
//                );
//            }
            try(InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream,destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return generatedFileName;

        }catch (IOException exception){
            throw new RuntimeException("Failed to store file",exception);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            //list all files in storageFolder
            //How to fix this ?
            return Files.walk(this.storageFolder, 1)
                    .filter(path -> !path.equals(this.storageFolder) && !path.toString().contains("._"))
                    .map(this.storageFolder::relativize);
        }
        catch (IOException exception) {
            throw new RuntimeException("Failed to load stored files", exception);
        }
    }

    @Override
    public byte[] readFileContent(String fileName) {
        try {
            Path file = storageFolder.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            }
            else {
                throw new RuntimeException(
                        "Could not read file: " + fileName);
            }
        }catch (IOException exception){
            throw new RuntimeException("Could not read file: " + fileName, exception); // neu ko co catch thi cac ham ton thoi gian se bao loi
        }
    }

    @Override
    public void deleteAllFiles() {

    }
}
