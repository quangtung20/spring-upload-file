package com.tutorial.apidemo.controllers;

import com.tutorial.apidemo.dto.ResponseDto;
import com.tutorial.apidemo.services.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/api/v1/FileUpload")
public class FileUploadController {
    @Autowired
    private IStorageService storageService;
    @PostMapping("")
    public ResponseEntity<ResponseDto> uploadFile(@RequestParam("file")MultipartFile file){
        try{
            String generatedFileName = storageService.storageFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseDto("ok","store file success",generatedFileName)
            );
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseDto("failed","store file failed",exception.getMessage())
            );
        }
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName){
        try {
            byte[] bytes = storageService.readFileContent(fileName);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception exception){
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<ResponseDto> getUploadedFiles() {
        try {
            List<String> urls = storageService.loadAll()
                    .map(path -> {
                        //convert fileName to url(send request "readDetailFile")
                        String urlPath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                        return urlPath;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseDto("ok", "List files successfully", urls));
        }catch (Exception exception) {
            return ResponseEntity.ok(new
                    ResponseDto("failed", "List files failed", new String[] {}));
        }
    }
}
