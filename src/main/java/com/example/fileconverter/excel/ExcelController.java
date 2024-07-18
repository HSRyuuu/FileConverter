package com.example.fileconverter.excel;


import com.example.fileconverter.common.iris.IrisDataObject;
import com.example.fileconverter.excel.dto.ExcelConvertResult;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ExcelController {

    private final ExcelService excelService;

    @ApiOperation(value = "IRIS에서 제공하는 data json 형식을 받아서 .xlsx 파일로 변환",
            notes = "key를 받아서 downloadExcelFile을 통해 다운로드 가능")
    @PostMapping("/api/convert/excel")
    public ResponseEntity<IrisDataObject> convertIrisJsonToXlsx(@RequestBody String json) throws IOException {
        IrisDataObject irisDataObject = IrisDataObject.fromIrisDataJson(json);
        //convert json to excel
        ExcelConvertResult excelConvertResult = excelService.createExcelFile(irisDataObject);
        //excel file을 찾을 수 있는 key
        String key = excelService.saveFile(excelConvertResult);

        return ResponseEntity.ok()
                .headers(excelService.getJsonHeader())
                .body(new IrisDataObject("key", key));
    }

    @ApiOperation(value = "key를 통해 excel file을 찾아서 반환", notes = "{key}.xlsx가 파일 명")
    @GetMapping("/api/download/excel")
    public ResponseEntity<byte[]> downloadExcelFile(@RequestParam(name = "key")String key) {
        ExcelConvertResult excelConvertResult = excelService.getFileByKey(key);
        return ResponseEntity.ok()
                .headers(excelService.getExcelHeader(excelConvertResult.getFileName()))
                .body(excelConvertResult.getExcelFile());
    }



}
