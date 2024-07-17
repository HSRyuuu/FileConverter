package com.example.fileconverter.web;

import com.example.fileconverter.excel.ExcelData;
import com.example.fileconverter.excel.ExcelResult;
import com.example.fileconverter.excel.ExcelService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class WebPageController {

    private final ExcelService excelService;

    @GetMapping("/excel")
    public String test() {

        return "excel";
    }

   @PostMapping("/convert/excel")
    public ResponseEntity<byte[]> test(@RequestParam String inputData) throws IOException {

        ExcelData excelData = excelService.parseJson(inputData);
        ExcelResult excelFile = excelService.createExcelFile(excelData);
        HttpHeaders headers = excelService.getExcelHeader(excelFile.getFileName());
       byte[] excelBytes = excelFile.getExcelBytes();
       //xxx

       return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile.getExcelBytes());
    }

}
