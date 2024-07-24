package com.example.fileconverter.excel;

import com.example.fileconverter.common.type.ConvertResult;
import com.example.fileconverter.common.iris.IrisDataObject;
import com.example.fileconverter.common.iris.IrisFieldObject;
import com.example.fileconverter.excel.dto.ExcelConvertResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelService {

    final String FILE_PATH = "/data/www/portal/htdocs/upload/";
    final String LOCAL_FILE_PATH = "C:\\dev_files\\";

    /**
     * IrisDataObject를 excel 파일로 변환
     *
     * @param irisData
     * @return
     */
    public ExcelConvertResult createExcelFile(IrisDataObject irisData) {
        String key = UUID.randomUUID().toString().substring(0, 10);
        String fileName = key + ".xlsx";

        List<String> headers =
                irisData.getFields().stream()
                        .map(IrisFieldObject::getName)
                        .collect(Collectors.toList()); //헤더(컬럼)
        List<List<String>> rows = irisData.getResults(); //데이터

        //Excel Sheet 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);

        //Setting Headers
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
        }

        //Setting Datas
        for (int i = 0; i < rows.size(); i++) {
            Row row = sheet.createRow(i + 1);
            List<String> data = rows.get(i);
            for (int j = 0; j < data.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data.get(j));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        byte[] excelBytes = outputStream.toByteArray();
        log.info("[Excel.xlsx File Created] fileName={} ", fileName);
        return new ExcelConvertResult(key, fileName, excelBytes);
    }


    /**
     * ExcelFile 전송을 위한 HTTP Header
     *
     * @param fileName
     * @return
     */
    public HttpHeaders getExcelHeader(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        return headers;
    }

    /**
     * Json 전송을 위한 HTTP Header
     *
     * @return
     */
    public HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Excel 파일 저장 후 ,key 반환
     *
     * @param excelConvertResult
     * @return
     */
    public String saveFile(ExcelConvertResult excelConvertResult) {
        try {
            String rootDirectory = "files";
            String fileName = excelConvertResult.getFileName();

            // 상대 경로 설정
            Path directoryPath = Paths.get(rootDirectory).toAbsolutePath().normalize();
            Files.createDirectories(directoryPath);

            // 파일 경로 설정
            String filePath = directoryPath + "/" + fileName;
            filePath =  FILE_PATH + fileName;

            // 파일 저장
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(excelConvertResult.getExcelFile());
            }
            return excelConvertResult.getKey();
        } catch (IOException e) {
            log.error("Error saving excel file");
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * key 값으로 파일을 찾아서 반환
     *
     * @param key : key.xlsx가 파일 명이다.
     * @return
     */
    public ExcelConvertResult getFileByKey(String key) {
        String fileName = key + ".xlsx";
        try {
            // 상대 경로 설정
            Path filePath = Paths.get(FILE_PATH + fileName).toAbsolutePath().normalize();

            File file = filePath.toFile();
            if (!file.exists()) {
                return new ExcelConvertResult(ConvertResult.ERROR);
            }

            byte[] excelFile = Files.readAllBytes(filePath);
            log.info("[Found Excel.xlsx Complete] fileName={} ", fileName);
            return new ExcelConvertResult(key, fileName, excelFile);
        } catch (IOException e) {
            log.error("[Excel.xlsx File NOT FOUND] key={})", key);
            e.printStackTrace();
            return new ExcelConvertResult(ConvertResult.ERROR);
        }
    }

}
