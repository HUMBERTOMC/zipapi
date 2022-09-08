package com.vercator.zipapi.controller;

import com.vercator.zipapi.exception.ZipApiException;
import com.vercator.zipapi.model.ZipFile;
import com.vercator.zipapi.service.ZipApiService;
import com.vercator.zipapi.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Slf4j
@RestController
public class ZipApiController {

    final ZipApiService zipService;

    public ZipApiController(ZipApiService zipService) {
        this.zipService = zipService;
    }

    @PostMapping(value = "/files/v1/zip")
    public ResponseEntity<?> zipFiles(
            @RequestParam("files") MultipartFile[] files){

        String requestId = UUID.randomUUID().toString();

        log.info("{} - Starting Zip Process for Request Id: {} and {} files",LogUtils.ZIP_API_PREFIX ,requestId, files.length);

        ZipFile zipFile;
        try {
            zipFile = zipService.zipFiles(files, requestId);
        } catch (ZipApiException e) {
            return ResponseEntity
                    .internalServerError()
                    .header("Content-Type","application/json")
                    .body(new JSONObject()
                    .put("error", e.getMessage()).toString());
        }

        return ResponseEntity.ok().body(zipFile);
    }

    @GetMapping(value = "/files/v1/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") String id){

        String fileName = "downloads/" + id + ".zip";
        InputStreamResource resource;
        File file;

        try {
            resource = new InputStreamResource(Files.newInputStream(Paths.get(fileName)));
            file = new File(fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().header("Content-Type","application/json").body(new JSONObject()
                    .put("error", "Didn't find the file:" + e.getMessage()).toString());
        }

        log.info("{} - Download file: {}",LogUtils.ZIP_API_PREFIX ,fileName);
        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"download.zip\"")
                .body(resource);

    }

    @RequestMapping("/files/v1/health")
    public ResponseEntity<String> health() {

        try {

            File directory = new File("downloads");
            if (!directory.exists()) {
                log.debug("Creating directory {}", directory.getAbsolutePath());
                if (!directory.mkdir()) {
                    throw new IOException("Create directory error");
                }
            }

            String fileName = "downloads/health_"+System.currentTimeMillis()+".txt";
            File file = new File(fileName);
            if (!file.createNewFile()) {
                throw new IOException("Create file error");
            }
            try (PrintStream out = new PrintStream(Files.newOutputStream(file.toPath()))) {
                out.print("ZIP API health check");
            }
            log.debug("File {} created", file.getAbsolutePath());

            if (!file.delete()) {
                throw new IOException("Delete file error");
            }
            log.debug("File {} deleted", file.getAbsolutePath());

            return ResponseEntity
                    .ok()
                    .body("Healthy");

        } catch (IOException e) {

            return ResponseEntity
                    .internalServerError().build();
        }
    }

}
