package com.demo.miniofilereader.service;

import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    Logger logger = LoggerFactory.getLogger(MinioService.class);
    public File prepareFilesZip(String bucketName) throws Exception {
        Path tempDir = Files.createTempDirectory("minioFiles");
        File zipFile = new File(tempDir.toFile(), "files.zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).recursive(true).build()
            );

            System.out.println(results);

            for (Result<Item> result: results) {
                Item item = result.get();
                File tempFile = new File(tempDir.toFile(), item.objectName());

                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }


                try (InputStream is = minioClient.getObject(
                        GetObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                     FileOutputStream fos = new FileOutputStream(tempFile)) {
                    is.transferTo(fos);
                } catch (FileNotFoundException e) {
                    System.err.println("Error with file at: " + tempFile.getAbsolutePath());
                    throw e;
                }

                try (FileInputStream fis = new FileInputStream(tempFile)) {
                    ZipEntry zipEntry = new ZipEntry(item.objectName());
                    zos.putNextEntry(zipEntry);
                    fis.transferTo(zos);
                    zos.closeEntry();
                }

                Files.delete(tempFile.toPath());  // Clean up the temporary file
            }
        }
//        catch (Exception e) {
//            logger.error("Exception during file processing", e);
//            throw e;
//        } finally {
//            // Ensure deletion of the temporary zip file
//            if (zipFile.exists()) {
//                boolean deleted = zipFile.delete();
//                if (!deleted) {
//                    logger.warn("Failed to delete temporary zip file: " + zipFile.getAbsolutePath());
//                } else {
//                    logger.info("Temporary zip file deleted successfully.");
//                }
//            }
//        }
        return zipFile;
    }
}
