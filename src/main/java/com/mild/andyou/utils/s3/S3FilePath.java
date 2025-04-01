package com.mild.andyou.utils.s3;


import com.mild.andyou.utils.Delimiter;

public class S3FilePath {

    public static String cdnUrl = "https://d2i5avakd0s2ny.cloudfront.net";
    public static String SURVEY_CONTENT_PATH = "surveys/contents";

    public static String getSurveyContentPath(String fileName) {
        if(fileName == null) {
            return null;
        }
        return Delimiter.SLASH.join(cdnUrl, SURVEY_CONTENT_PATH, fileName);
    }

    public static String getSurveyContentKey(String fileName) {
        return Delimiter.SLASH.join(SURVEY_CONTENT_PATH, fileName);
    }

}