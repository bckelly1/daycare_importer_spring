package com.brian.daycare_importer_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@Setter
@AllArgsConstructor
// Basic POJO, is not persisted
public class MailMessage {
    private String from;
    private String to;
    private String subject;
    private String body;
    private String html;
    private String messageId;
    private String label;
    private Map<String, String> headers;
}
