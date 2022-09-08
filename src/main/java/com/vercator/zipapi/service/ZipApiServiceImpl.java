package com.vercator.zipapi.service;


import com.vercator.zipapi.exception.ZipApiException;
import com.vercator.zipapi.model.ZipFile;
import com.vercator.zipapi.utils.EnvUtil;
import com.vercator.zipapi.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ZipApiServiceImpl implements ZipApiService {

    @Autowired
    private EnvUtil envUtil;

    @Override
    public ZipFile zipFiles(MultipartFile[] files, String requestId) throws ZipApiException {

        File directory = new File("downloads");
        if (! directory.exists()){
            directory.mkdir();
        }

        String fileName = "downloads/" + requestId + ".zip";
        File file = new File(fileName);
        String lastFileName = "";
        long totalSize = 0;
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fos);
            for (MultipartFile mFile : files) {
                lastFileName = mFile.getOriginalFilename();
                zipOutputStream.putNextEntry( new ZipEntry(lastFileName));
                IOUtils.copy(mFile.getInputStream(), zipOutputStream);
                mFile.getInputStream().close();
                zipOutputStream.closeEntry();
                totalSize += mFile.getSize();
                log.info("{} - Request Id: {} - Zipping File: {} Size: {} MB ",LogUtils.ZIP_API_PREFIX ,requestId, mFile.getOriginalFilename(), getSizeMB(mFile.getSize()));
            }
            zipOutputStream.close();

        } catch (IOException e) {
            file.delete();
            String errorMessage = MessageFormat.format("{0} - Request Id: {1} - Error trying to zip files with follow error {2}", LogUtils.ZIP_API_PREFIX, requestId, e.getMessage());
            log.error(errorMessage);
            throw new ZipApiException(errorMessage);
        }

       log.info("{} - Request Id: {} - Process Completed Successfully: Total Size {} MB -> Zipped {} MB ",LogUtils.ZIP_API_PREFIX, requestId, getSizeMB(totalSize),getSizeMB(file.length()));
       return ZipFile.builder().id(requestId).downloadURI(hostName(requestId)).sizeMB(getSizeMB(file.length())).build();
    }

    private float getSizeMB(long size){
        if (size != 0){
            return ((float)(size/1024)/1024);
        }
        return size;
    }

    private String hostName(String requestId){
        try {
            return  envUtil.getServerUrlPrefi() + "/files/v1/download/" + requestId;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }
}
