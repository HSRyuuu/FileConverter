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
import org.springframework.beans.factory.annotation.Value;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelService {

    @Value("${excel.file.path}")
    private String FILE_PATH; //absolute path

    /**
     * {보고서명}_yyyy년mm월기준 <- 파일 이름 생성
     * @param reportName : 보고서명
     * @param referenceDate :  yyyy-mm-dd 형태
     * @return
     */
    public String createFileName(String reportName, String referenceDate) {
        final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        if (!pattern.matcher(referenceDate).matches()) {
            log.error("Pattern Error: reference_date wrong pattern => yyyy-mm-dd required");
            throw new IllegalArgumentException("[Pattern Error] reference_date wrong pattern => yyyy-mm-dd required");
        }
        return new StringBuilder(reportName).append("_")
                .append(referenceDate.substring(0, 4)).append("년")
                .append(referenceDate.substring(5, 7)).append("월기준")
                .toString();
    }

    /**
     * 엑셀 파일 생성, 저장 후 식별할 수 있는 key를 반환
     * @param irisData
     * @param key
     * @return key
     */
    public String createExcelFile(IrisDataObject irisData, String key) {
        String fileName = key + ".xlsx";
        if(this.fileExists(fileName)){
            log.info("File Already Exists: fileName={}, path={} => just return key", fileName, FILE_PATH);

            return this.getRandomizedKey(key);
        }
        log.info("File Not Exists: create file start");

        List<String> headers =
                irisData.getFields().stream()
                        .map(IrisFieldObject::getName)
                        .collect(Collectors.toList()); //헤더(컬럼)
        List<List<String>> rows = irisData.getResults(); //데이터

        byte[] excelBytes = this.createExcelBytes(fileName, headers, rows);

        this.saveFile(fileName, excelBytes);

        return this.getRandomizedKey(key);
    }

    /**
     * 파일 존재 확인
     * @param fileName
     * @return
     */
    private boolean fileExists(String fileName) {
        File file = new File(FILE_PATH, fileName);
        return file.exists() && file.isFile();
    }

    /**
     * excel file byte[] 생성
     * @param fileName
     * @param headers
     * @param rows
     * @return
     */
    private byte[] createExcelBytes(String fileName, List<String> headers, List<List<String>> rows){
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
            outputStream.close();
        } catch (IOException e) {
            log.error("ByteArrayOutputStream Error: writing excel file", e);
        }

        log.info("Excel File Created: fileName={} ", fileName);
        return outputStream.toByteArray();
    }


    /**
     * Excel 파일 저장 후 ,key 반환
     *
     * @return
     */
    private void saveFile(String fileName, byte[] excelFile) {
        try {
            // 파일 경로 설정
            String filePath = FILE_PATH + "/" + fileName;
            //파일 저장
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(excelFile);
            fos.close();
            log.info("Save File Complete: path={}", filePath);
        } catch (IOException e) {
            log.error("File Write Error: write excel file on FileOutputStream", e);
        }

    }

    /**
     * key 값으로 파일을 찾아서 반환
     *
     * @param key : key.xlsx가 파일 명이다.
     * @return
     */
    public ExcelConvertResult getFileByKey(String key) {
        String fileName = getKey(key) + ".xlsx";
        try {
            // 상대 경로 설정
            Path filePath = Paths.get(FILE_PATH + "/" + fileName).toAbsolutePath().normalize();

            File file = filePath.toFile();
            if (!file.exists()) {
                return new ExcelConvertResult(ConvertResult.ERROR);
            }

            byte[] excelFile = Files.readAllBytes(filePath);
            log.info("Find ExcelFile Complete: path ={}/{} ", FILE_PATH, fileName);
            return new ExcelConvertResult(key, fileName, excelFile);
        } catch (IOException e) {
            log.error("Excel File NOT FOUND: key={})", key);
            return new ExcelConvertResult(ConvertResult.ERROR);
        }
    }

    private String getRandomizedKey(String key) {
        return UUID.randomUUID().toString().substring(0, 4) + key;
    }

    private String getKey(String randomizedKey){
        return randomizedKey.substring(4);
    }

    /**
     * 상대 경로 반환 (상대경로 사용 시)
     * @param fileName
     * @return
     * @throws IOException
     */
    private String getRelativePath(String fileName) throws IOException {
        String rootDirectory = "files";
        // 상대 경로 설정
        Path directoryPath = Paths.get(rootDirectory).toAbsolutePath().normalize();
        Files.createDirectories(directoryPath);
        return directoryPath +  "/" + fileName;
    }

}
