package com.brian.daycare_importer_spring;

import com.brian.daycare_importer_spring.entity.MailMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestBaseUtils {
    public String loadFileContents(String fileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(fileName)) {
            if (is != null) {
                return new String(is.readAllBytes());
            }
            else{
                throw new RuntimeException("Problem reading file: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MailMessage createMockMailMessage() {
        String to = "to@example.com";
        String from = "from@example.com";
        String subject = "subject";
        String body = "";
        String html = "";
        String label = "";
        String messageId = "messageId";
        Map<String, String> headers = new HashMap<>();
        return new MailMessage(to, from, subject, body, html, messageId, label, headers);
    }
}
