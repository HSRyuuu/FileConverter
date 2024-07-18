package com.example.fileconverter.common.iris;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Builder
public class IrisDataObject {
    private String jobId;
    private String recordCount;
    private List<IrisFieldObject> fields;
    private List<List<String>> results;
    private String status;

    public IrisDataObject(){
        this.jobId = "";
        this.recordCount = "0";
        this.fields = new ArrayList<>();
        this.results = new ArrayList<>();
        this.status = "END";
    }

    public IrisDataObject(String field, String result) {
        this.jobId = "transport object";
        this.recordCount = "1";
        IrisFieldObject ifo = new IrisFieldObject(field, "TEXT");
        this.fields = new ArrayList<>(Arrays.asList(ifo));
        this.results = new ArrayList<>();
        this.results.add(new ArrayList<>(Arrays.asList(result)));
        this.status = "END";
    }

    public static IrisDataObject fromIrisDataJson(String irisDataJson){
        IrisDataObject irisDataObject = new IrisDataObject();
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            JsonNode root = objectMapper.readTree(irisDataJson);
            //META-DATA
            irisDataObject.setJobId(root.get("job_id").asText()); //IRIS job id
            irisDataObject.setRecordCount(root.get("record_count").asText());

            //Excel Header
            List<IrisFieldObject> fields = new ArrayList<>();
            ArrayNode fieldsNode = (ArrayNode) root.get("fields");
            for (JsonNode node : fieldsNode) {
                String name = node.get("name").asText(); //field name
                String type = node.get("type").asText(); //field type
                irisDataObject.getFields().add(new IrisFieldObject(name, type));
            }

            //Excel rows
            List<List<String>> results = new ArrayList<>();
            for (JsonNode node : root.get("results")) {
                List<String> row = new ArrayList<>();
                Iterator<JsonNode> nodeIterator = node.elements();

                while(nodeIterator.hasNext()){
                    JsonNode element = nodeIterator.next();
                    String field = "";
                    if(!element.isNull()){
                        field = element.asText();
                    }
                    row.add(field);
                }

                results.add(row);
            }
            irisDataObject.setResults(results);

        }catch (IOException e){
            log.error("[IrisDataJson] parsing error");
            e.printStackTrace();
            return new IrisDataObject();
        }
        return irisDataObject;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IrisDataObject_" + this.hashCode());
        sb.append("\n- jobId = " + this.jobId);
        sb.append("\n- recordCount = " + this.recordCount);
        sb.append("\n- fields\n");
        for (IrisFieldObject field : this.fields) {
            sb.append("  [name:" + field.getName() + ", type:" + field.getType() + "]");
        }
        sb.append("\n- results");
        for ( List<String> row : this.results) {
            sb.append("\n  " + row.toString());
        }
        return sb.toString();
    }
}
