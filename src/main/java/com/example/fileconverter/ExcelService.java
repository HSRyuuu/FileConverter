package com.example.fileconverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class ExcelService {

    public ExcelResult createExcelFile(ExcelData excelData) throws IOException {
        List<String> headers = excelData.getHeaders();
        List<List<String>> dataSet = excelData.getDataSet();

        String fileName = LocalDate.now() + "_" + excelData.getJobId() + ".xlsx";

        //Excel Sheet 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);

        //Setting Headers
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < headers.size(); i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
        }

        //Setting Datas
        for(int i =0 ; i < dataSet.size(); i++) {
            Row row = sheet.createRow(i + 1);
            List<String> data = dataSet.get(i);
            for(int j = 0; j < data.size(); j++){
                Cell cell = row.createCell(j);
                cell.setCellValue(data.get(j));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] excelBytes = outputStream.toByteArray();

        return new ExcelResult(fileName, excelBytes);
    }

    public ExcelData parseJson(String json){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(json);
            String jobId = root.get("job_id").asText();
            int recordCount = root.get("record_count").asInt();

            List<String> fields = parseHeader(root);
            List<List<String>> results = parseDataSet(root);

            ExcelData excelData = new ExcelData(jobId, recordCount,fields, results);
            System.out.println(excelData);
            printParsingResult(excelData);
            return excelData;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ExcelData("error",0, new ArrayList<>(), new ArrayList<>(new ArrayList<>()));
    }



    public List<String> parseHeader(JsonNode jsonNode){
        ArrayNode fieldsNode = (ArrayNode) jsonNode.get("fields");
        List<String> fields = new ArrayList<>();
        for (JsonNode node : fieldsNode) {
            String name = node.get("name").asText();
            fields.add(name);
        }
        return fields;
    }

    private List<List<String>> parseDataSet(JsonNode root) {
        List<List<String>> results = new ArrayList<>();

        for (JsonNode node : root.get("results")) {
            List<String> result = new ArrayList<>();

            Iterator<JsonNode> nodeIterator = node.elements();
            while(nodeIterator.hasNext()){
                JsonNode element = nodeIterator.next();
                String field = "";
                if(!element.isNull()){
                    field = element.asText();
                }
                result.add(field);
            }
            results.add(result);
        }

        return results;
    }

    public HttpHeaders getExcelHeader(String fileName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        return headers;
    }


    public void printParsingResult(ExcelData excelData){
        log.info("> header : {}", excelData.getHeaders());
        for(List<String> data : excelData.getDataSet()){
            log.info("> data : {}", data);
        }
    }


}
