package com.mild.andyou.utils.s3;


import com.mild.andyou.utils.Delimiter;

public class S3FilePath {

    public static String CDN_URL = "https://d2i5avakd0s2ny.cloudfront.net";
    public static String SURVEY_CONTENT_PATH = "surveys/contents";

    public static String CDN_URL_SURVEY_CONTENT_PATH = CDN_URL+"/"+SURVEY_CONTENT_PATH;

    public static String getSurveyContentKey(String fileName) {
        return Delimiter.SLASH.join(SURVEY_CONTENT_PATH, fileName);
    }

}