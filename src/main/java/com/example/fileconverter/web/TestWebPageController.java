package com.example.fileconverter.web;

import com.example.fileconverter.common.iris.IrisDataObject;
import com.example.fileconverter.excel.dto.ExcelConvertResult;
import com.example.fileconverter.excel.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class TestWebPageController {

    private final ExcelService excelService;

    @GetMapping("/excel")
    public String test() {

        return "excel";
    }

   @PostMapping("/convert/excel")
    public ResponseEntity<byte[]> test(@RequestParam String inputData) throws IOException {
        IrisDataObject irisDataObject = IrisDataObject.fromIrisDataJson(inputData);

       ExcelConvertResult excelConvertResult = excelService.createExcelFile(irisDataObject);
       HttpHeaders headers = excelService.getExcelHeader(excelConvertResult.getFileName());
       byte[] excelBytes = excelConvertResult.getExcelFile();

       return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

}
