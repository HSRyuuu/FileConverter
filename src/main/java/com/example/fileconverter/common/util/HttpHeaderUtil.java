package com.example.fileconverter.common.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class HttpHeaderUtil {
    /**
     * ExcelFile 전송을 위한 HTTP Header
     *
     * @param fileName
     * @return
     */
    public static HttpHeaders getExcelHeader(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        return headers;
    }

    /**
     * Json 전송을 위한 HTTP Header
     *
     * @return
     */
    public static HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
