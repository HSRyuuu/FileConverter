package com.example.fileconverter.excel;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ExcelController {

    private final ExcelService excelService;

    @PostMapping("/api/convert/excel")
    public ResponseEntity<byte[]> convertJsonToXlsx(@RequestBody String json) throws IOException {
        ExcelData excelData = excelService.parseJson(json);
        ExcelResult excelFile = excelService.createExcelFile(excelData);
        HttpHeaders headers = excelService.getExcelHeader(excelFile.getFileName());
        // 추가 파일 저장 로직 등
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile.getExcelBytes());
    }

    @PostMapping("/api/convert/excel/test")
    public ResponseEntity<String> test(@RequestBody String json) throws IOException {
        ExcelData excelData = excelService.parseJson(json);
        ExcelResult excelFile = excelService.createExcelFile(excelData);
        HttpHeaders headers = excelService.getExcelHeader(excelFile.getFileName());
        // xxx
        return ResponseEntity.ok()
                .body(excelFile.getFileName());
    }

}
