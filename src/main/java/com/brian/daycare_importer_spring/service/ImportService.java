package com.brian.daycare_importer_spring.service;

import com.brian.daycare_importer_spring.config.MailConfig;
import com.brian.daycare_importer_spring.entity.MailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {
    @Autowired
    private GmailService gmailService;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private ImageDownloadService imageDownloadService;

    public void runSunshineHouseImport() {
        MailMessage[] unreadMessages = gmailService.getUnreadMessages(mailConfig.getSunshineHouseLabel());
        log.info("Unread messages count: {}", unreadMessages.length);

        for (MailMessage unreadMessage : unreadMessages) {
            handleSunshineHouseSummaryEmail(unreadMessage);
            gmailService.markAsRead(unreadMessage);
        }
    }

    public void runBrightHorizonsImport() {
        MailMessage[] unreadMessages = gmailService.getUnreadMessages(mailConfig.getBrightHorizonsLabel());
        log.info("Unread messages count: {}", unreadMessages.length);

        for (MailMessage unreadMessage : unreadMessages) {
            handleBrightHorizonsDaycareSummaryEmail(unreadMessage);
            gmailService.markAsRead(unreadMessage);
        }
    }

    private void handleBrightHorizonsDaycareSummaryEmail(MailMessage mailMessage) {
        String date = mailMessage.getHeaders().get("Date");
        log.info("Parsing email from {}", date);

        Document document = Jsoup.parse(mailMessage.getHtml());

        // Child name is now in a td with class "child-name"
        String childName = document.select("td.child-name").text().strip().split(" ")[0];

        // Select only snapshot/activity images from the Bright Horizons media API,
        // excluding the profile picture (which uses thumbnail=true)
        Elements images = document.body().select(
                "img[src*='mbdgw.brighthorizons.com/api/parent/medias/v1/media/m/snapshot/']"
        );
        log.info("Located {} images", images.size());

        for (Element image : images) {
            String imageSource = image.attributes().get("src");

            // Strip query parameters to get the original quality image
            // e.g. https://mbdgw.brighthorizons.com/.../snapshot/abc123?d=t&enail=true
            //   -> https://mbdgw.brighthorizons.com/.../snapshot/abc123
            String imageOriginal = imageSource.contains("?")
                    ? imageSource.substring(0, imageSource.indexOf("?"))
                    : imageSource;

            log.info(imageOriginal);

            String[] imageOriginalParts = imageOriginal.split("/");
            String imageName = imageOriginalParts[imageOriginalParts.length - 1];

            String imageDirectory = "images/" + childName;
            String imagePath = imageDirectory + "/" + imageName + ".jpg";
            Long time = Long.parseLong(mailMessage.getHeaders().get("Custom-Epoch")) * 1000L;

            // Re-add the d=t to the end to strip out the extra divs
            String downloadPath = imageOriginal + "?d=t";
            imageDownloadService.downloadImage(downloadPath, imagePath);
            imageDownloadService.modifyImageDownloadTimestamp(imagePath, time);
        }
    }

// Reads the content of the email from Daycare, extract child name and pictures. The URLs of the pictures are S3 objects.
//   The original objects are "medium" quality but by guessing the URLs, you can get the original quality photos. Each
//   Photo is then downloaded and saved.
    private void handleSunshineHouseSummaryEmail(MailMessage mailMessage) {
        String date = mailMessage.getHeaders().get("Date");
        log.info("Parsing email from {}", date);

        Document document = Jsoup.parse(mailMessage.getHtml());
        String childName = document.select("span.emailH1").text().strip().split(" ")[0];
        Elements images = document.body().select("img.resize_img1");
        log.info("Located {} images", images.size());

        for(Element image : images) {
            String imageSource = image.attributes().get("src");
            String imageOriginal;
            if(imageSource.contains("thumbnails")) {
                //This is a video !The path is different !
                imageOriginal = imageSource.replace("thumbnails", "video").replace("-00001.png", ".mp4");
            }
            else {
                // Just a normal photo
                imageOriginal = imageSource.replace("medium", "original");
            }
            log.info(imageOriginal);

            String[] imageOriginalParts = imageOriginal.split("/");
            String imageName = imageOriginalParts[imageOriginalParts.length - 1];

            // TODO: It would be cool to extract the exact time for the images, but that 's difficult right now

            String imageDirectory = "images/" + childName;
            String imagePath = imageDirectory + "/" + imageName;
            Long time = Long.parseLong(mailMessage.getHeaders().get("Custom-Epoch")) * 1000L;

            imageDownloadService.downloadImage(imageOriginal, imagePath);
            imageDownloadService.modifyImageDownloadTimestamp(imagePath, time);
        }
    }
}
