package com.example.fileconverter.excel;


import com.example.fileconverter.common.iris.IrisDataObject;
import com.example.fileconverter.common.util.HttpHeaderUtil;
import com.example.fileconverter.excel.dto.ExcelConvertResult;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class ExcelController {

    private final ExcelService excelService;

    @ApiOperation(value = "IRIS에서 제공하는 data json 형식을 받아서 .xlsx 파일로 변환",
            notes = "key를 받아서 downloadExcelFile을 통해 다운로드 가능")
    @PostMapping("/api/convert/excel")
    public ResponseEntity<IrisDataObject> convertIrisJsonToXlsx(@RequestBody String irisDataJson,
                                                                @RequestParam(value = "name") String reportName,
                                                                @RequestParam(value = "date") String referenceDate){
        String fileName = excelService.createFileName(reportName, referenceDate);
        IrisDataObject irisDataObject = IrisDataObject.fromIrisDataJson(irisDataJson);//Json 형식 그대로 파싱하여 객체로 변환
        String key = excelService.createExcelFile(irisDataObject, fileName);

        return ResponseEntity.ok()
                .headers(HttpHeaderUtil.getJsonHeader())
                .body(new IrisDataObject("key", key));
    }

    @ApiOperation(value = "key를 통해 excel file을 찾아서 반환", notes = "{key}.xlsx가 파일 명")
    @GetMapping("/api/download/excel")
    public ResponseEntity<byte[]> downloadExcelFile(@RequestParam(name = "key")String key) {
        ExcelConvertResult excelConvertResult = excelService.getFileByKey(key);

        return ResponseEntity.ok()
                .headers(HttpHeaderUtil.getExcelHeader(excelConvertResult.getFileName()))
                .body(excelConvertResult.getExcelFile());
    }



}
