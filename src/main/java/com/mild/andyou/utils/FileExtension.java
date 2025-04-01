package com.mild.andyou.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

@Getter
@AllArgsConstructor
public enum FileExtension {

    PNG(".png", MediaType.IMAGE_PNG),
    JPG(".jpg", MediaType.IMAGE_JPEG),
    JPEG(".jpeg", MediaType.IMAGE_JPEG),
    GIF(".gif", MediaType.IMAGE_GIF),
    WEBM(".webm", MediaType.parseMediaType("video/webm")),
    MP4(".mp4", MediaType.parseMediaType("video/mp4")),
    TXT(".txt", MediaType.TEXT_PLAIN),
    DOC(".doc", MediaType.parseMediaType("application/msword")),
    DOCX(".docx", MediaType.parseMediaType("application/msword")),
    PDF(".pdf", MediaType.APPLICATION_PDF),
    XLSX(".xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    ZIP(".zip", MediaType.parseMediaType("application/zip"));

    private final String value;
    private final MediaType mediaType;

    public static MediaType getMediaType(String fileName) {
        FileExtension fileExtension = getFileExtension(fileName);
        return fileExtension != null ? fileExtension.getMediaType() : MediaType.APPLICATION_OCTET_STREAM;
    }

    public static FileExtension getFileExtension(String fileName) {
        String[] strings = fileName.split(Delimiter.DOT.getRegex());
        int length = strings.length;

        if (strings.length < 2) {
            throw new IllegalArgumentException("The file name must include the extension.");
        }

        for (FileExtension extension : FileExtension.values()) {
            if (extension.name().equalsIgnoreCase(strings[length - 1])) {
                return extension;
            }
        }

        return null;
    }

}