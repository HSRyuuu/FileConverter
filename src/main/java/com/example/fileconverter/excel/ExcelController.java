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
    public ResponseEntity<byte[]> test(@RequestBody String json) throws IOException {
        ExcelData excelData = excelService.parseJson(json);
        ExcelResult excelFile = excelService.createExcelFile(excelData);
        HttpHeaders headers = excelService.getExcelHeader(excelFile.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile.getExcelBytes());
    }

}
