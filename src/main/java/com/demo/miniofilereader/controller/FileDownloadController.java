package com.demo.miniofilereader.controller;

import com.demo.miniofilereader.service.MinioService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.concurrent.Callable;

@RestController
public class FileDownloadController {

    private final MinioService minioService;

    public FileDownloadController(MinioService minioService) {
        this.minioService = minioService;
    }

//    @GetMapping("/download/{bucketName}")
//    public ResponseEntity<Resource> downloadFiles(@PathVariable String bucketName) throws Exception {
//        File zipFile = minioService.prepareFilesZip(bucketName);
//        Resource fileSystemResource = new FileSystemResource(zipFile);
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=" + zipFile.getName())
//                .body(fileSystemResource);
//    }

    @GetMapping("/download/{bucketName}")
    public Callable<ResponseEntity<Resource>> downloadFiles(@PathVariable String bucketName) {
        return () -> {
            File fileZip = minioService.prepareFilesZip(bucketName);
            Resource fileSystemResource = new FileSystemResource(fileZip);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileZip.getName() + "\"")
                    .body(fileSystemResource);
        };
    }
}
