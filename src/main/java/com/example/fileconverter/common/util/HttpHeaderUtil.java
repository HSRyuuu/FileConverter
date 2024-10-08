package com.example.fileconverter.common.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

public class HttpHeaderUtil {
    /**
     * ExcelFile 전송을 위한 HTTP Header
     *
     * @param fileName
     * @return ExcelFile 전송 시 HTTP Headers
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
     * @return Json 전송 시 HTTP Headers
     */
    public static HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
