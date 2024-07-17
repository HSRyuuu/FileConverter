package com.example.fileconverter;

import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelData {

    private String jobId;
    private int recordCount;
    private List<String> headers;
    private List<List<String>> dataSet;
}
