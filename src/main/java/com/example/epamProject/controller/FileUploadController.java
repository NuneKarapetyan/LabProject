package com.example.epamProject.controller;

import com.example.epamProject.service.FileSystemStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {
    private final FileSystemStorageService fileStorageService;

    public FileUploadController(FileSystemStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestBody MultipartFile file
    ) {
        String fileName = fileStorageService.storeFile(file);
        System.out.println(fileName);
        UploadResponse uploadResponse = new UploadResponse(fileName);
        return ResponseEntity.ok().body(uploadResponse);
    }

}
