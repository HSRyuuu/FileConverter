package com.example.fileconverter.common;

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
public class IrisDataObject {
    private String jobId;
    private String recordCount;
    private IrisFieldObject[] fields;
    private String[][] results;
    private String status;

    public IrisDataObject(String field, String result) {
        this.jobId = "none";
        this.recordCount = "1";
        IrisFieldObject ifo = new IrisFieldObject(field, "TEXT");
        this.fields = new IrisFieldObject[]{ifo};
        this.results = new String[1][1];
        this.results[0][0] = result;
        this.status = "END";
    }
}
