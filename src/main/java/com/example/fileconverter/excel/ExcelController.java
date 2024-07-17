package com.example.fileconverter.excel;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
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

    @PostMapping("/api/test/upload")
    public ResponseEntity<String> test(@RequestBody String json) throws IOException {
        ExcelData excelData = excelService.parseJson(json);
        ExcelResult excelFile = excelService.createExcelFile(excelData);
        HttpHeaders headers = excelService.getExcelHeader(excelFile.getFileName());
        String fileName = excelService.saveFile(excelFile);
        return ResponseEntity.ok()
                .body(fileName);
    }

    @GetMapping("/api/test/download")
    public ResponseEntity<byte[]> saveExcelFile(@RequestParam(name = "nm")String fileName) {
        ExcelResult excelResult = excelService.getFileByFileName(fileName);
        return ResponseEntity.ok()
                .headers(excelService.getExcelHeader(fileName))
                .body(excelResult.getExcelBytes());
    }

}
