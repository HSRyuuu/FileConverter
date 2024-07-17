package com.example.fileconverter;


import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ExcelController {

    private final ExcelService excelService;

    @PostMapping("/convert-excel")
    public ResponseEntity<byte[]> test(@RequestBody String json) throws IOException {
        ExcelData excelData = excelService.parseJson(json);
        ExcelResult excelFile = excelService.createExcelFile(excelData);
        HttpHeaders headers = excelService.getExcelHeader(excelFile.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile.getExcelBytes());
    }

}
