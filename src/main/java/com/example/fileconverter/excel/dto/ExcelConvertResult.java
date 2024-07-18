package com.example.fileconverter.excel.dto;

import com.example.fileconverter.common.type.ConvertResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelConvertResult {
    private String key;
    private String fileName;
    private byte[] excelFile; // 실제 엑셀 파일

    public ExcelConvertResult(ConvertResult convertResult) {
        if(convertResult == ConvertResult.ERROR){
            this.key = "error";
            this.fileName = "error.xlsx";
            this.excelFile = new byte[]{};
        }
    }
}
