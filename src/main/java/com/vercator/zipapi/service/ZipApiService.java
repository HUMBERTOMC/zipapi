package com.vercator.zipapi.service;

import com.vercator.zipapi.exception.ZipApiException;
import com.vercator.zipapi.model.ZipFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

public interface ZipApiService {

    ZipFile zipFiles(MultipartFile[] files, String requestId) throws ZipApiException;
}
