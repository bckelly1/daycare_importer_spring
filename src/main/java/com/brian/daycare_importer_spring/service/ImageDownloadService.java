package com.brian.daycare_importer_spring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class ImageDownloadService {

    private final RestTemplate restTemplate;

    public ImageDownloadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void downloadImage(String url, String destinationFilePath) {
        Resource resource = restTemplate.getForObject(url, UrlResource.class);

        if (resource != null && resource.exists()) {
            Path destinationPath = Paths.get(destinationFilePath);
            try {
                Files.copy(resource.getInputStream(), destinationPath);
            } catch (IOException e) {
                log.error("Failed to download image from {}", url, e);
            }
        } else {
            log.error("Failed to download image from {}", url);
        }
    }

    public void modifyImageDownloadTimestamp(String filePath, String newCreationDate) {

        try {
            Path path = Paths.get(filePath);
            FileTime fileTime = FileTime.fromMillis(parseDate(newCreationDate).getTime());

            // Update basic file attributes
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);

            view.write("creationTime", ByteBuffer.wrap(fileTime.toString().getBytes()));

            // If the filesystem supports setting creation time
            Files.setAttribute(path, "basic:creationTime", fileTime);
        }
        catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Date parseDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(date);
    }
}
