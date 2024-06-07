package com.brian.daycare_importer_spring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ImageDownloadService {

    private final RestTemplate restTemplate;

    public ImageDownloadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void downloadImage(String url, String destinationFilePath) {
        log.debug("Copying file to {}", destinationFilePath);

        try {
            // Ensure the parent directory exists
            Path destinationPath = Paths.get(destinationFilePath);
            Files.createDirectories(destinationPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RequestCallback requestCallback = request -> request.getHeaders().setAccept(List.of(org.springframework.http.MediaType.ALL));

        ResponseExtractor<Void> responseExtractor = response -> {
            try (InputStream inputStream = response.getBody();
                FileOutputStream outputStream = new FileOutputStream(destinationFilePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            return null;
        };

        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    public void modifyImageDownloadTimestamp(String filePath, Long newCreationDate) {

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

    private Date parseDate(Long time) throws ParseException {
        return new Date(time);
    }
}

